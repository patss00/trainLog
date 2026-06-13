import json
import mimetypes
import os
from contextlib import asynccontextmanager
from datetime import datetime
from pathlib import Path
from typing import Optional

import requests
from dotenv import load_dotenv
from fastapi import Body, Depends, FastAPI, HTTPException
from fastapi.responses import FileResponse
from fastapi.staticfiles import StaticFiles
from pydantic import BaseModel
from sqlalchemy import (
    Boolean,
    Column,
    DateTime,
    ForeignKey,
    Integer,
    String,
    create_engine,
    text,
)
from sqlalchemy.orm import Session, declarative_base, sessionmaker


# ============================================================
# CONNECTION / ENVIRONMENT
# ============================================================

BASE_DIR = Path(__file__).resolve().parent
ENV_PATH = BASE_DIR / ".env"

load_dotenv(ENV_PATH)

DATABASE_URL = os.getenv("DATABASE_URL")
SUPABASE_URL = os.getenv("SUPABASE_URL")
SUPABASE_SERVICE_KEY = os.getenv("SUPABASE_SERVICE_KEY")

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
        "cat_has": sticker.cat_has,
        "pat_has": sticker.pat_has,
    }


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
    finally:
        db.close()

    yield


app = FastAPI(lifespan=lifespan)


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
        "screen": [],
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


@app.get("/screen")
def get_screen():
    return data.get("screen", [])


@app.get("/pic")
def get_pic():
    file_path = static_dir / "pic.jpg"

    if not file_path.exists():
        return {"error": f"File not found: {file_path}"}

    return FileResponse(str(file_path), media_type="image/jpg")


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
    sticker_id: int,
    item: StickerStatusUpdate,
    db: Session = Depends(get_db),
):
    person = item.person.lower().strip()

    if person not in ["pat", "cat"]:
        raise HTTPException(
            status_code=400,
            detail="person must be either 'pat' or 'cat'",
        )

    image_files = list_sticker_files_from_supabase()

    if sticker_id < 1 or sticker_id > len(image_files):
        raise HTTPException(status_code=404, detail="Sticker not found")

    file = image_files[sticker_id - 1]
    file_name = file["name"]

    sticker = ensure_sticker_exists(db, sticker_id, file_name)

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


@app.get("/stickers/{sticker_id}/image")
def get_sticker_image(sticker_id: int):
    file_path = static_dir / "stickers" / f"{sticker_id}.jpg"

    if not file_path.exists():
        raise HTTPException(status_code=404, detail="Sticker image not found")

    return FileResponse(str(file_path), media_type="image/jpg")