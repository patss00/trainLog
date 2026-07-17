import json
import mimetypes
import os
import random
from contextlib import asynccontextmanager
from datetime import datetime, date, time
from pathlib import Path
from typing import Optional
from passlib.context import CryptContext
import requests
from dotenv import load_dotenv
from fastapi import Body, Depends, FastAPI, File, HTTPException, UploadFile
from fastapi.responses import FileResponse
from fastapi.staticfiles import StaticFiles
from fastapi import Response
from pydantic import BaseModel
from fastapi.middleware.cors import CORSMiddleware
import re
#from matplotlib import pyplot as plt
from sqlalchemy import Boolean, Column, Date, DateTime, ForeignKey, Integer, String, Time, create_engine, text, func, Float
from sqlalchemy.orm import Session, declarative_base, sessionmaker
from wordfreq import zipf_frequency


# ============================================================
# CONNECTION / ENVIRONMENT
# ============================================================

BASE_DIR = Path(__file__).resolve().parent
ENV_PATH = BASE_DIR / ".env"

load_dotenv(ENV_PATH)

DATABASE_URL = os.getenv("DATABASE_URL")
SUPABASE_URL = os.getenv("SUPABASE_URL")
SUPABASE_SERVICE_KEY = os.getenv("SUPABASE_SERVICE_KEY")
OCR_SPACE_API_KEY = os.getenv("OCR_SPACE_API_KEY")

if not DATABASE_URL:
    raise RuntimeError("DATABASE_URL is not set in .env")

if not SUPABASE_URL:
    raise RuntimeError("SUPABASE_URL is not set in .env")

if not SUPABASE_SERVICE_KEY:
    raise RuntimeError("SUPABASE_SERVICE_KEY is not set in .env")


# ============================================================
# DATABASE SETUP
# ============================================================

engine = create_engine(DATABASE_URL)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
Base = declarative_base()
pwd_context = CryptContext(schemes=["pbkdf2_sha256"], deprecated="auto")


def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()


# ============================================================
# TEXTS FEATURE
# ============================================================

class Text(Base):
    __tablename__ = "texts"

    id = Column(Integer, primary_key=True, index=True)
    content = Column(String, nullable=False)
    person = Column(String, nullable=False)
    created_at = Column(DateTime, default=datetime.now)


class TextCreate(BaseModel):
    content: Optional[str] = None
    person: Optional[str] = None


# ============================================================
# LOGS FEATURE
# ============================================================

class Log(Base):
    __tablename__ = "logs"

    id = Column(Integer, primary_key=True, index=True)
    type = Column(String, nullable=False)
    count = Column(Integer, nullable=False)
    date = Column(DateTime, default=datetime.now)


class LogCreate(BaseModel):
    type: Optional[str] = None
    count: Optional[int] = None


# ============================================================
# NOTES FEATURE
# ============================================================

class Notes(Base):
    __tablename__ = "notes"

    id = Column(Integer, primary_key=True, autoincrement=True)
    content = Column(String, nullable=False)


class NoteOut(BaseModel):
    content: str


# ============================================================
# TASKS FEATURE
# ============================================================

class Tasks(Base):
    __tablename__ = "tasks"

    id_task = Column(Integer, primary_key=True, autoincrement=True)
    description_task = Column(String, nullable=False)
    isDone = Column(Boolean, nullable=False, default=False)
    suggest = Column(Boolean, nullable=False, default=False)
    date_task = Column(String, nullable=False)


class TasksCreate(BaseModel):
    description_task: Optional[str] = None
    isDone: Optional[bool] = None
    suggest: Optional[bool] = None
    date_task: Optional[str] = None


class TaskOut(BaseModel):
    id_task: int
    description_task: str
    isDone: bool
    suggest: bool
    date_task: str


# ============================================================
# SCREEN STATE FEATURE
# ============================================================

class ScreenState(Base):
    __tablename__ = "screen_state"

    id = Column(Integer, primary_key=True, index=True)
    cat_open_card = Column(Boolean, nullable=False, default=True)
    pat_open_card = Column(Boolean, nullable=False, default=True)
    updated = Column(Boolean, nullable=False, default=False)


class CatOpenCardUpdate(BaseModel):
    catOpenCard: bool


class PatOpenCardUpdate(BaseModel):
    patOpenCard: bool


class UpdatedUpdate(BaseModel):
    updated: bool


def get_or_create_screen_state(db: Session):
    state = db.query(ScreenState).filter(ScreenState.id == 1).first()

    if not state:
        state = ScreenState(
            id=1,
            cat_open_card=True,
            pat_open_card=True,
            updated=False,
        )
        db.add(state)
        db.commit()
        db.refresh(state)

    return state

# ============================================================
# STICKERS FEATURE
# ============================================================

class Category(Base):
    __tablename__ = "categories"

    id = Column(Integer, primary_key=True, index=True)
    description = Column(String, nullable=False)


class Sticker(Base):
    __tablename__ = "stickers"

    id = Column(Integer, primary_key=True, index=True)
    name = Column(String, nullable=False)
    image_file = Column(String, nullable=False)
    category_id = Column(Integer, ForeignKey("categories.id"), nullable=True)
    is_portrait = Column(Boolean, nullable=False, default=True)
    cat_has = Column(Boolean, nullable=False, default=False)
    pat_has = Column(Boolean, nullable=False, default=False)


class CategoryCreate(BaseModel):
    description: str


class StickerStatusUpdate(BaseModel):
    person: str
    status: bool


def list_sticker_files_from_supabase():
    bucket_name = "stickers"

    response = requests.post(
        f"{SUPABASE_URL}/storage/v1/object/list/{bucket_name}",
        headers={
            "Authorization": f"Bearer {SUPABASE_SERVICE_KEY}",
            "apikey": SUPABASE_SERVICE_KEY,
            "Content-Type": "application/json",
        },
        json={
            "prefix": "",
            "limit": 1000,
            "offset": 0,
            "sortBy": {
                "column": "name",
                "order": "asc",
            },
        },
    )

    if response.status_code != 200:
        raise HTTPException(
            status_code=500,
            detail=f"Could not list stickers: {response.text}",
        )

    files = response.json()

    image_files = [
        file for file in files
        if file["name"].lower().endswith((".jpg", ".jpeg", ".png", ".webp"))
    ]

    image_files.sort(key=lambda file: file["name"])

    return image_files


def get_sticker_public_url(file_name: str):
    bucket_name = "stickers"
    return f"{SUPABASE_URL}/storage/v1/object/public/{bucket_name}/{file_name}"


def ensure_sticker_exists(db: Session, sticker_id: int, file_name: str):
    sticker = db.query(Sticker).filter(Sticker.id == sticker_id).first()

    if not sticker:
        sticker = Sticker(
            id=sticker_id,
            name=f"Cromo {sticker_id}",
            image_file=file_name,
            category_id=None,
            is_portrait=True,
            cat_has=False,
            pat_has=False,
        )
        db.add(sticker)
        db.commit()
        db.refresh(sticker)
    else:
        if sticker.image_file != file_name:
            sticker.image_file = file_name
            db.commit()
            db.refresh(sticker)

    return sticker


def build_sticker_response(db: Session, sticker: Sticker):
    category = None

    if sticker.category_id is not None:
        category = db.query(Category).filter(
            Category.id == sticker.category_id
        ).first()

    return {
        "id": sticker.id,
        "name": sticker.name,
        "image_url": get_sticker_public_url(sticker.image_file),
        "image_file": sticker.image_file,
        "isPortrait": sticker.is_portrait,
        "categoryId": sticker.category_id,
        "category": category.description if category else None,
        "cat_has": sticker.cat_has,
        "pat_has": sticker.pat_has,
    }

# ============================================================
# APP PASSWORD FEATURE
# ============================================================

class AppPassword(Base):
    __tablename__ = "app_password"

    id = Column(Integer, primary_key=True, index=True)
    password_hash = Column(String, nullable=False)


class PasswordCheck(BaseModel):
    password: str


class PasswordSet(BaseModel):
    password: str


def get_app_password(db: Session):
    return db.query(AppPassword).filter(AppPassword.id == 1).first()

# ============================================================
# EVENTS FEATURE
# ============================================================

class Event(Base):
    __tablename__ = "events"

    event_id = Column(Integer, primary_key=True, index=True)
    date = Column(Date, nullable=False)
    description = Column(String, nullable=False)
    start_time = Column(Time, nullable=True)
    end_time = Column(Time, nullable=True)


class EventCreate(BaseModel):
    event_id: Optional[int] = None
    date: date
    description: str
    start_time: Optional[time] = None
    end_time: Optional[time] = None

class EventsListCreate(BaseModel):
    events: list[EventCreate]

# ============================================================
# SCHEDULE IMAGE FEATURE
# ============================================================

class ScheduleImageFile(BaseModel):
    name: str
    bytes: list[int]


class ScheduleImagesRequest(BaseModel):
    files: list[ScheduleImageFile]


class ProcessedScheduleFile(BaseModel):
    filename: str
    size_bytes: int


class ProcessScheduleImagesResponse(BaseModel):
    count: int
    files: list[ProcessedScheduleFile]

# ============================================================
# Others
# ============================================================

class ValidUrl(Base):
    __tablename__ = "urls"

    valid = Column(String, primary_key=True, index=True)

class UrlCheck(BaseModel):
    url: str

class Word(Base):
    __tablename__ = "words"

    ws = Column(String, primary_key=True, index=True)
    lang_code = Column("langCode", String, primary_key=True, index=True)

class RealWordCheck(BaseModel):
    word: str
    langCode: str = "en"

class Person(Base):
    __tablename__ = "people"

    id = Column(Integer, primary_key=True, index=True)
    person = Column(String, nullable=False)


class Transaction(Base):
    __tablename__ = "transactions"

    id = Column(Integer, primary_key=True, index=True)
    description = Column(String, nullable=False)
    amount = Column(Float, nullable=False)
    person = Column(Integer, ForeignKey("people.id"), nullable=False)
    transaction_type = Column("type", String, nullable=False)

class TransactionCreate(BaseModel):
    description: str
    amount: float
    person: int
    type: str
# ============================================================
# CREATE TABLES
# ============================================================

Base.metadata.create_all(bind=engine)


# ============================================================
# FASTAPI APP / STARTUP
# ============================================================

@asynccontextmanager
async def lifespan(app: FastAPI):
    db = SessionLocal()
    try:
        if db.query(Notes).first() is None:
            db.add(Notes(content=""))
            db.commit()

        get_or_create_screen_state(db)
    finally:
        db.close()

    yield


app = FastAPI(lifespan=lifespan)
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=False,
    allow_methods=["*"],
    allow_headers=["*"],
)


@app.get("/")
def root():
    return {"status": "ok"}


@app.head("/")
def root_head():
    return Response(status_code=200)

# ============================================================
# STATIC / JSON FILES
# ============================================================

stations_path = BASE_DIR / "stations.json"

if stations_path.exists():
    with open(stations_path, "r", encoding="utf-8") as f:
        data = json.load(f)
else:
    data = {
        "cutes": [],
        "screen": {},
    }

mimetypes.add_type("image/jpg", ".jpg")

static_dir = BASE_DIR / "static"

if static_dir.exists():
    app.mount("/static", StaticFiles(directory=str(static_dir)), name="static")


# ============================================================
# GENERAL ROUTES
# ============================================================

@app.get("/")
def root():
    return {"message": "Train API is running"}


@app.get("/db-test")
def db_test(db: Session = Depends(get_db)):
    result = db.execute(text("select now();"))

    return {
        "connected": True,
        "database_time": str(result.scalar()),
    }


@app.get("/cutes")
def get_all_cutes():
    return data.get("cutes", [])


@app.get("/pic")
def get_pic():
    file_path = static_dir / "pic.jpg"

    if not file_path.exists():
        return {"error": f"File not found: {file_path}"}

    return FileResponse(str(file_path), media_type="image/jpg")


# ============================================================
# SCREEN ROUTES
# ============================================================

@app.get("/screen")
def get_screen(db: Session = Depends(get_db)):
    screen_data = data.get("screen", {}).copy()
    state = get_or_create_screen_state(db)

    screen_data.pop("openCard", None)

    screen_data["catOpenCard"] = state.cat_open_card
    screen_data["patOpenCard"] = state.pat_open_card
    screen_data["updated"] = state.updated

    return screen_data


@app.get("/screen/cat-open-card")
def get_cat_open_card(db: Session = Depends(get_db)):
    state = get_or_create_screen_state(db)

    return {
        "catOpenCard": state.cat_open_card,
    }


@app.put("/screen/cat-open-card")
def update_cat_open_card(
    item: CatOpenCardUpdate,
    db: Session = Depends(get_db),
):
    state = get_or_create_screen_state(db)
    state.cat_open_card = item.catOpenCard

    db.commit()
    db.refresh(state)

    return {
        "catOpenCard": state.cat_open_card,
    }


@app.get("/screen/pat-open-card")
def get_pat_open_card(db: Session = Depends(get_db)):
    state = get_or_create_screen_state(db)

    return {
        "patOpenCard": state.pat_open_card,
    }


@app.put("/screen/pat-open-card")
def update_pat_open_card(
    item: PatOpenCardUpdate,
    db: Session = Depends(get_db),
):
    state = get_or_create_screen_state(db)
    state.pat_open_card = item.patOpenCard

    db.commit()
    db.refresh(state)

    return {
        "patOpenCard": state.pat_open_card,
    }


@app.put("/screen/updated")
def update_screen_updated(
    item: UpdatedUpdate,
    db: Session = Depends(get_db),
):
    state = get_or_create_screen_state(db)
    state.updated = item.updated

    db.commit()
    db.refresh(state)

    return {
        "updated": state.updated,
    }

# ============================================================
# TEXT ROUTES
# ============================================================

@app.post("/texts")
def create_text(item: TextCreate, db: Session = Depends(get_db)):
    if not item.content or not item.person:
        raise HTTPException(
            status_code=400,
            detail="content and person are required",
        )

    db_text = Text(content=item.content, person=item.person)
    db.add(db_text)
    db.commit()
    db.refresh(db_text)

    return {
        "id": db_text.id,
        "content": db_text.content,
        "person": db_text.person,
        "created_at": db_text.created_at,
    }


@app.get("/texts")
def get_texts(db: Session = Depends(get_db)):
    texts = db.query(Text).order_by(Text.created_at.desc()).all()

    return [
        {
            "id": t.id,
            "content": t.content,
            "person": t.person,
            "created_at": t.created_at,
        }
        for t in texts
    ]


@app.delete("/texts/{text_id}")
def delete_text(text_id: int, db: Session = Depends(get_db)):
    db_text = db.query(Text).filter(Text.id == text_id).first()

    if not db_text:
        raise HTTPException(status_code=404, detail="Text not found")

    db.delete(db_text)
    db.commit()

    return {"message": f"Text with id {text_id} has been deleted."}


# ============================================================
# LOG ROUTES
# ============================================================

@app.post("/logs")
def create_log(item: LogCreate, db: Session = Depends(get_db)):
    if not item.type or item.count is None:
        raise HTTPException(
            status_code=400,
            detail="type and count are required",
        )

    db_log = Log(type=item.type, count=item.count)
    db.add(db_log)
    db.commit()
    db.refresh(db_log)

    return {
        "id": db_log.id,
        "type": db_log.type,
        "count": db_log.count,
        "date": db_log.date,
    }


@app.get("/logs")
def get_logs(db: Session = Depends(get_db)):
    logs = db.query(Log).order_by(Log.date.desc()).all()

    return [
        {
            "id": l.id,
            "type": l.type,
            "count": l.count,
            "date": l.date,
        }
        for l in logs
    ]


# ============================================================
# NOTE ROUTES
# ============================================================

@app.put("/note", response_model=NoteOut)
def update_note(body: dict = Body(...), db: Session = Depends(get_db)):
    content = body.get("content", "")

    note = db.query(Notes).first()

    if not note:
        note = Notes(content=content)
        db.add(note)
    else:
        note.content = content

    db.commit()
    db.refresh(note)

    return {"content": note.content}


@app.get("/note", response_model=NoteOut)
def get_note(db: Session = Depends(get_db)):
    note = db.query(Notes).first()

    if not note:
        note = Notes(content="")
        db.add(note)
        db.commit()
        db.refresh(note)

    return {"content": note.content}


@app.get("/debug/notes")
def debug_notes(db: Session = Depends(get_db)):
    notes = db.query(Notes).all()

    return [
        {
            "id": n.id,
            "content": n.content,
        }
        for n in notes
    ]


@app.get("/debug/reset_note")
def reset_note(db: Session = Depends(get_db)):
    note = db.query(Notes).first()

    if note:
        note.content = ""
    else:
        note = Notes(content="")
        db.add(note)

    db.commit()
    db.refresh(note)

    return {
        "id": note.id,
        "content": note.content,
    }


# ============================================================
# TASK ROUTES
# ============================================================

@app.post("/tasks")
def create_task(item: TasksCreate, db: Session = Depends(get_db)):
    if not item.description_task or not item.date_task:
        raise HTTPException(
            status_code=400,
            detail="description_task and date_task are required",
        )

    db_task = Tasks(
        description_task=item.description_task,
        isDone=item.isDone if item.isDone is not None else False,
        suggest=item.suggest if item.suggest is not None else False,
        date_task=item.date_task,
    )

    db.add(db_task)
    db.commit()
    db.refresh(db_task)

    return {
        "id_task": db_task.id_task,
        "description_task": db_task.description_task,
        "isDone": db_task.isDone,
        "suggest": db_task.suggest,
        "date_task": db_task.date_task,
    }


@app.get("/tasks")
def get_tasks(db: Session = Depends(get_db)):
    tasks = db.query(Tasks).all()

    return [
        {
            "id_task": t.id_task,
            "description_task": t.description_task,
            "isDone": t.isDone,
            "suggest": t.suggest,
            "date_task": t.date_task,
        }
        for t in tasks
    ]


@app.put("/tasks", response_model=TaskOut)
def update_task(body: dict = Body(...), db: Session = Depends(get_db)):
    id_task = body.get("id_task")
    isDone = body.get("isDone")

    if id_task is None:
        raise HTTPException(status_code=400, detail="id_task is required")

    task = db.query(Tasks).filter(Tasks.id_task == id_task).first()

    if not task:
        raise HTTPException(status_code=404, detail="Task not found")

    if isDone is not None:
        task.isDone = isDone

    db.commit()
    db.refresh(task)

    return {
        "id_task": task.id_task,
        "description_task": task.description_task,
        "isDone": task.isDone,
        "suggest": task.suggest,
        "date_task": task.date_task,
    }


# ============================================================
# CATEGORY ROUTES
# ============================================================

@app.post("/categories")
def create_category(item: CategoryCreate, db: Session = Depends(get_db)):
    if not item.description:
        raise HTTPException(
            status_code=400,
            detail="description is required",
        )

    category = Category(description=item.description)
    db.add(category)
    db.commit()
    db.refresh(category)

    return {
        "id": category.id,
        "description": category.description,
    }


@app.get("/categories")
def get_categories(db: Session = Depends(get_db)):
    categories = db.query(Category).order_by(Category.id.asc()).all()

    return [
        {
            "id": c.id,
            "description": c.description,
        }
        for c in categories
    ]


# ============================================================
# STICKER ROUTES
# ============================================================

@app.get("/stickers")
def get_stickers(db: Session = Depends(get_db)):
    image_files = list_sticker_files_from_supabase()
    stickers = []

    for index, file in enumerate(image_files, start=1):
        file_name = file["name"]
        sticker = ensure_sticker_exists(db, index, file_name)
        stickers.append(build_sticker_response(db, sticker))

    return stickers


@app.get("/stickers/random-missing")
def get_random_missing_sticker(person: str, db: Session = Depends(get_db)):
    person = person.lower().strip()

    if person not in ["pat", "cat"]:
        raise HTTPException(
            status_code=400,
            detail="person must be either 'pat' or 'cat'",
        )

    image_files = list_sticker_files_from_supabase()

    for index, file in enumerate(image_files, start=1):
        file_name = file["name"]
        ensure_sticker_exists(db, index, file_name)

    if person == "pat":
        missing_stickers = db.query(Sticker).filter(
            Sticker.pat_has == False
        ).all()
    else:
        missing_stickers = db.query(Sticker).filter(
            Sticker.cat_has == False
        ).all()

    if not missing_stickers:
        raise HTTPException(
            status_code=404,
            detail=f"{person} already has all stickers",
        )

    selected_sticker = random.choice(missing_stickers)

    return build_sticker_response(db, selected_sticker)


@app.get("/stickers/{sticker_id}")
def get_sticker_by_id(sticker_id: int, db: Session = Depends(get_db)):
    image_files = list_sticker_files_from_supabase()

    if sticker_id < 1 or sticker_id > len(image_files):
        raise HTTPException(status_code=404, detail="Sticker not found")

    file = image_files[sticker_id - 1]
    file_name = file["name"]

    sticker = ensure_sticker_exists(db, sticker_id, file_name)

    return build_sticker_response(db, sticker)


@app.put("/stickers/{sticker_id}/status")
def update_sticker_status(
    sticker_id: str,
    item: StickerStatusUpdate,
    db: Session = Depends(get_db),
):
    try:
        sticker_id_int = int(sticker_id)
    except ValueError:
        raise HTTPException(
            status_code=400,
            detail="sticker_id must be a valid number",
        )

    person = item.person.lower().strip()

    if person not in ["pat", "cat"]:
        raise HTTPException(
            status_code=400,
            detail="person must be either 'pat' or 'cat'",
        )

    image_files = list_sticker_files_from_supabase()

    if sticker_id_int < 1 or sticker_id_int > len(image_files):
        raise HTTPException(status_code=404, detail="Sticker not found")

    file = image_files[sticker_id_int - 1]
    file_name = file["name"]

    sticker = ensure_sticker_exists(db, sticker_id_int, file_name)

    if person == "pat":
        sticker.pat_has = item.status

    if person == "cat":
        sticker.cat_has = item.status

    db.commit()
    db.refresh(sticker)

    return {
        "id": sticker.id,
        "cat_has": sticker.cat_has,
        "pat_has": sticker.pat_has,
    }


@app.get("/stickers/{sticker_id}/image")
def get_sticker_image(sticker_id: int):
    file_path = static_dir / "stickers" / f"{sticker_id}.jpg"

    if not file_path.exists():
        raise HTTPException(status_code=404, detail="Sticker image not found")

    return FileResponse(str(file_path), media_type="image/jpg")

# ============================================================
# APP PASSWORD ROUTES
# ============================================================

@app.post("/password/check")
def check_password(
    item: PasswordCheck,
    db: Session = Depends(get_db),
):
    saved_password = get_app_password(db)

    if not saved_password:
        raise HTTPException(
            status_code=404,
            detail="App password has not been set",
        )

    is_correct = pwd_context.verify(
        item.password,
        saved_password.password_hash,
    )

    return {
        "valid": is_correct,
    }


@app.put("/password")
def set_password(
    item: PasswordSet,
    db: Session = Depends(get_db),
):
    if not item.password:
        raise HTTPException(
            status_code=400,
            detail="password is required",
        )

    password_hash = pwd_context.hash(item.password)

    saved_password = get_app_password(db)

    if not saved_password:
        saved_password = AppPassword(
            id=1,
            password_hash=password_hash,
        )
        db.add(saved_password)
    else:
        saved_password.password_hash = password_hash

    db.commit()
    db.refresh(saved_password)

    return {
        "message": "Password updated",
    }

# ============================================================
# EVENT ROUTES
# ============================================================

@app.get("/events")
def get_events(db: Session = Depends(get_db)):
    events = db.query(Event).order_by(
        Event.date.asc(),
        Event.start_time.asc().nullsfirst(),
    ).all()

    return [
        {
            "event_id": event.event_id,
            "date": event.date.isoformat(),
            "description": event.description,
            "start_time": format_event_time(event.start_time),
            "end_time": format_event_time(event.end_time),
        }
        for event in events
    ]


@app.put("/events")
def put_event(item: EventCreate, db: Session = Depends(get_db)):
    if not item.description:
        raise HTTPException(
            status_code=400,
            detail="description is required",
        )

    if item.end_time <= item.start_time:
        raise HTTPException(
            status_code=400,
            detail="end_time must be after start_time",
        )

    if item.event_id is not None:
        event = db.query(Event).filter(Event.event_id == item.event_id).first()

        if not event:
            raise HTTPException(
                status_code=404,
                detail="Event not found",
            )

        event.date = item.date
        event.description = item.description
        event.start_time = item.start_time
        event.end_time = item.end_time

    else:
        event = Event(
            date=item.date,
            description=item.description,
            start_time=item.start_time,
            end_time=item.end_time,
        )

        db.add(event)

    db.commit()
    db.refresh(event)

    return {
        "event_id": event.event_id,
        "date": event.date.isoformat(),
        "description": event.description,
        "start_time": event.start_time.strftime("%H:%M"),
        "end_time": event.end_time.strftime("%H:%M"),
    }

# ============================================================
# SCHEDULE IMAGE ROUTES
# ============================================================
def extract_events_from_ocr_text(raw_text: str):
    lines_raw = raw_text.splitlines()

    cleaned_lines = []

    for line in lines_raw:
        clean_line = line.strip()

        if clean_line:
            cleaned_lines.append(clean_line)

    ignored_lines = [
        "Schedule",
        "Planned shifts",
        "Worked shifts",
        "Upcoming",
        "Pay Period",
        "Upcoming months",
        "PORT CHIADO",
        "This week",
        "Next week",
        "Info",
        "My shifts",
        "Shift Exchange",
        "My Absence",
        "Menu",
        "|||",
    ]

    lines = [
        line
        for line in cleaned_lines
        if line not in ignored_lines and "Select" not in line
    ]

    days = ["mon", "tue", "wed", "thu", "fri", "sat", "sun"]

    months = [
        "jan", "feb", "mar", "apr", "may", "jun",
        "jul", "aug", "sep", "oct", "nov", "dec"
    ]

    time_range_pattern = re.compile(
        r"(\d{1,2}:\d{2})\s*[-–—]\s*(\d{1,2}:\d{2})"
    )

    def normalize_time(value: str):
        hour, minute = value.split(":")
        return f"{int(hour):02d}:{minute}"

    events = []

    for index, line in enumerate(lines):
        if line.lower() not in days:
            continue

        if index + 2 >= len(lines):
            continue

        day_text = lines[index + 1].strip()
        month_text = lines[index + 2].strip().lower()[:3]

        if not day_text.isdigit():
            continue

        if month_text not in months:
            continue

        month_int = months.index(month_text) + 1

        month = str(month_int).zfill(2)
        day = day_text.zfill(2)

        event_date = f"2026-{month}-{day}"

        description = None
        start_time = None
        end_time = None

        search_index = index + 3

        while search_index < len(lines):
            candidate_line = lines[search_index].strip()
            candidate_lower = candidate_line.lower()

            if candidate_lower in days:
                break

            if "día libre" in candidate_lower or "dia libre" in candidate_lower:
                description = "folga"
                start_time = None
                end_time = None
                break

            time_match = time_range_pattern.search(candidate_line)

            if time_match:
                description = "trabalho"
                start_time = normalize_time(time_match.group(1))
                end_time = normalize_time(time_match.group(2))
                break

            search_index += 1

        if description is None:
            continue

        events.append({
            "event_id": None,
            "date": event_date,
            "description": description,
            "start_time": start_time,
            "end_time": end_time,
        })

    return events

def is_all_day_event(start_time, end_time):
    if start_time is None and end_time is None:
        return True

    if start_time == end_time:
        return True

    if (
        start_time is not None
        and end_time is not None
        and start_time.strftime("%H:%M") == "00:00"
        and end_time.strftime("%H:%M") == "23:59"
    ):
        return True

    return False


def format_event_time(value):
    if value is None:
        return None

    return value.strftime("%H:%M")

@app.post("/schedule-images/process")
async def process_schedule_images(
    files: list[UploadFile] = File(...),
):
    if not OCR_SPACE_API_KEY:
        raise HTTPException(
            status_code=500,
            detail="OCR_SPACE_API_KEY is not set",
        )

    processed_files = []
    all_events = []

    for file in files:
        contents = await file.read()

        if not contents:
            raise HTTPException(
                status_code=400,
                detail=f"{file.filename} is empty",
            )

        ocr_response = requests.post(
            "https://api.ocr.space/parse/image",
            headers={
                "apikey": OCR_SPACE_API_KEY,
            },
            files={
                "file": (
                    file.filename or "schedule.png",
                    contents,
                    file.content_type or "image/png",
                )
            },
            data={
                "language": "eng",
                "isOverlayRequired": "false",
                "detectOrientation": "true",
                "scale": "true",
                "OCREngine": "2",
            },
            timeout=90,
        )

        if ocr_response.status_code != 200:
            raise HTTPException(
                status_code=500,
                detail=f"OCR request failed: {ocr_response.text}",
            )

        ocr_data = ocr_response.json()

        if ocr_data.get("IsErroredOnProcessing"):
            raise HTTPException(
                status_code=500,
                detail=ocr_data.get("ErrorMessage", "OCR processing failed"),
            )

        parsed_results = ocr_data.get("ParsedResults", [])

        extracted_text = ""

        if parsed_results:
            extracted_text = parsed_results[0].get("ParsedText", "")

        events = extract_events_from_ocr_text(extracted_text)

        processed_files.append({
            "filename": file.filename,
            "text": extracted_text,
            "events": events,
        })

        all_events.extend(events)

    return {
        "count": len(processed_files),
        "files": processed_files,
        "events": all_events,
    }

@app.put("/events/list")
def put_events_list(
    item: EventsListCreate,
    db: Session = Depends(get_db),
):
    if not item.events:
        raise HTTPException(
            status_code=400,
            detail="events list is required",
        )

    # Validate duplicate dates only inside this API request
    received_dates = set()

    for event_item in item.events:
        if event_item.date in received_dates:
            raise HTTPException(
                status_code=400,
                detail=f"Duplicate date in received events: {event_item.date.isoformat()}",
            )

        received_dates.add(event_item.date)

    saved_events = []

    for event_item in item.events:
        if not event_item.description:
            raise HTTPException(
                status_code=400,
                detail="description is required",
            )

        start_time = event_item.start_time
        end_time = event_item.end_time

        if is_all_day_event(start_time, end_time):
            start_time = None
            end_time = None
        else:
            if start_time is None or end_time is None:
                raise HTTPException(
                    status_code=400,
                    detail="start_time and end_time are both required for non-all-day events",
                )

            if end_time <= start_time:
                raise HTTPException(
                    status_code=400,
                    detail="end_time must be after start_time",
                )

        if event_item.event_id is None:
            event = Event(
                date=event_item.date,
                description=event_item.description,
                start_time=start_time,
                end_time=end_time,
            )

            db.add(event)

        else:
            event = db.query(Event).filter(
                Event.event_id == event_item.event_id
            ).first()

            if not event:
                raise HTTPException(
                    status_code=404,
                    detail=f"Event {event_item.event_id} not found",
                )

            event.date = event_item.date
            event.description = event_item.description
            event.start_time = start_time
            event.end_time = end_time

        saved_events.append(event)

    db.commit()

    for event in saved_events:
        db.refresh(event)

    return {
        "saved": len(saved_events),
        "events": [
            {
                "event_id": event.event_id,
                "date": event.date.isoformat(),
                "description": event.description,
                "start_time": format_event_time(event.start_time),
                "end_time": format_event_time(event.end_time),
            }
            for event in saved_events
        ],
    }


# ============================================================
# Others
# ============================================================

@app.post("/urls/check")
def check_url(
    item: UrlCheck,
    db: Session = Depends(get_db),
):
    try:
        if not item.url:
            return {
                "valid": False,
                "error": "URL is required",
            }

        received_url = item.url.strip()

        if not received_url:
            return {
                "valid": False,
                "error": "URL is required",
            }

        # Optional: allow match with or without final slash
        possible_urls = {
            received_url,
            received_url.rstrip("/"),
            received_url.rstrip("/") + "/",
        }

        match = db.query(ValidUrl).filter(
            ValidUrl.valid.in_(possible_urls)
        ).first()

        if match:
            return {
                "valid": True,
                "error": None,
            }

        return {
            "valid": False,
            "error": "URL is not valid",
        }

    except Exception as e:
        return {
            "valid": False,
            "error": str(e),
        }
    
@app.get("/words/random")
def get_random_word(
    langCode: str = "en",
    db: Session = Depends(get_db),
):
    try:
        clean_lang_code = langCode.strip().lower()

        if clean_lang_code not in ["en", "pt"]:
            return {
                "word": None,
                "langCode": clean_lang_code,
                "error": "Invalid language. Use en or pt.",
            }

        word = db.query(Word).filter(
            Word.lang_code == clean_lang_code
        ).order_by(
            func.random()
        ).first()

        if not word:
            return {
                "word": None,
                "langCode": clean_lang_code,
                "error": "No words found for this language.",
            }

        return {
            "word": word.ws,
            "langCode": word.lang_code,
            "error": None,
        }

    except Exception as e:
        return {
            "word": None,
            "langCode": langCode,
            "error": str(e),
        }
    
@app.post("/words/check-real")
def check_real_word(item: RealWordCheck):
    try:
        clean_word = item.word.strip().lower()
        clean_lang_code = item.langCode.strip().lower()

        if not clean_word:
            return {
                "valid": False,
                "word": clean_word,
                "langCode": clean_lang_code,
                "score": 0,
                "error": "Word is required",
            }

        if len(clean_word) != 5:
            return {
                "valid": False,
                "word": clean_word,
                "langCode": clean_lang_code,
                "score": 0,
                "error": "Word must have 5 letters",
            }

        if clean_lang_code not in ["en", "pt"]:
            return {
                "valid": False,
                "word": clean_word,
                "langCode": clean_lang_code,
                "score": 0,
                "error": "Invalid language. Use en or pt.",
            }

        score = zipf_frequency(clean_word, clean_lang_code)

        if score > 0:
            return {
                "valid": True,
                "word": clean_word,
                "langCode": clean_lang_code,
                "score": score,
                "error": None,
            }

        return {
            "valid": False,
            "word": clean_word,
            "langCode": clean_lang_code,
            "score": score,
            "error": "Word not found",
        }

    except Exception as e:
        return {
            "valid": False,
            "word": item.word,
            "langCode": item.langCode,
            "score": 0,
            "error": str(e),
        }
    
@app.post("/transactions")
def create_transaction(
    item: TransactionCreate,
    db: Session = Depends(get_db),
):
    try:
        if not item.description.strip():
            return {
                "success": False,
                "transaction": None,
                "error": "Description is required",
            }

        if item.amount <= 0:
            return {
                "success": False,
                "transaction": None,
                "error": "Amount must be greater than 0",
            }

        if not item.type.strip():
            return {
                "success": False,
                "transaction": None,
                "error": "Type is required",
            }

        person = db.query(Person).filter(
            Person.id == item.person
        ).first()

        if not person:
            return {
                "success": False,
                "transaction": None,
                "error": "Person not found",
            }

        transaction = Transaction(
            description=item.description.strip(),
            amount=item.amount,
            person=item.person,
            transaction_type=item.type.strip(),
        )

        db.add(transaction)
        db.commit()
        db.refresh(transaction)

        return {
            "success": True,
            "transaction": {
                "id": transaction.id,
                "description": transaction.description,
                "amount": transaction.amount,
                "person": transaction.person,
                "personName": person.person,
                "type": transaction.transaction_type,
            },
            "error": None,
        }

    except Exception as e:
        return {
            "success": False,
            "transaction": None,
            "error": str(e),
        }