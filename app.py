import json
import mimetypes
import os
from contextlib import asynccontextmanager
from datetime import datetime
from pathlib import Path
from typing import Optional

from dotenv import load_dotenv
from fastapi import Body, Depends, FastAPI, HTTPException
from fastapi.responses import FileResponse
from fastapi.staticfiles import StaticFiles
from pydantic import BaseModel
from sqlalchemy import Boolean, Column, DateTime, Integer, String, create_engine, text
from sqlalchemy.orm import Session, declarative_base, sessionmaker


# --- Paths and environment ---

BASE_DIR = Path(__file__).resolve().parent
ENV_PATH = BASE_DIR / ".env"

load_dotenv(ENV_PATH)

DATABASE_URL = os.getenv("DATABASE_URL")

if not DATABASE_URL:
    raise RuntimeError("DATABASE_URL is not set in .env")


# --- Database setup ---

engine = create_engine(DATABASE_URL)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
Base = declarative_base()


# --- Models ---

class Text(Base):
    __tablename__ = "texts"

    id = Column(Integer, primary_key=True, index=True)
    content = Column(String, nullable=False)
    person = Column(String, nullable=False)
    created_at = Column(DateTime, default=datetime.now)


class Log(Base):
    __tablename__ = "logs"

    id = Column(Integer, primary_key=True, index=True)
    type = Column(String, nullable=False)
    count = Column(Integer, nullable=False)
    date = Column(DateTime, default=datetime.now)


class Notes(Base):
    __tablename__ = "notes"

    id = Column(Integer, primary_key=True, autoincrement=True)
    content = Column(String, nullable=False)


class Tasks(Base):
    __tablename__ = "tasks"

    id_task = Column(Integer, primary_key=True, autoincrement=True)
    description_task = Column(String, nullable=False)
    isDone = Column(Boolean, nullable=False, default=False)
    suggest = Column(Boolean, nullable=False, default=False)
    date_task = Column(String, nullable=False)


# --- Create tables if they do not exist ---

Base.metadata.create_all(bind=engine)


# --- FastAPI app with lifespan ---

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


# --- Load static JSON data ---

stations_path = BASE_DIR / "stations.json"

if stations_path.exists():
    with open(stations_path, "r", encoding="utf-8") as f:
        data = json.load(f)
else:
    data = {
        "cutes": [],
        "screen": []
    }


# --- Static files ---

mimetypes.add_type("image/jpg", ".jpg")

static_dir = BASE_DIR / "static"

if static_dir.exists():
    app.mount("/static", StaticFiles(directory=str(static_dir)), name="static")


# --- Dependencies ---

def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()


# --- Schemas ---

class TextCreate(BaseModel):
    content: Optional[str] = None
    person: Optional[str] = None


class LogCreate(BaseModel):
    type: Optional[str] = None
    count: Optional[int] = None


class NoteOut(BaseModel):
    content: str


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


# --- General routes ---

@app.get("/")
def root():
    return {"message": "Train API is running"}


@app.get("/db-test")
def db_test(db: Session = Depends(get_db)):
    result = db.execute(text("select now();"))
    return {
        "connected": True,
        "database_time": str(result.scalar())
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

@app.get("/stickers")
def get_stickers():
    return [
        {
            "id": i,
            "name": f"Cromo {i}",
            "image_url": f"/stickers/{i}/image",
            "cat_has": False,
            "pat_has": False
        }
        for i in range(1, 11)
    ]

# --- Text routes ---

@app.post("/texts")
def create_text(item: TextCreate, db: Session = Depends(get_db)):
    if not item.content or not item.person:
        raise HTTPException(status_code=400, detail="content and person are required")

    db_text = Text(content=item.content, person=item.person)
    db.add(db_text)
    db.commit()
    db.refresh(db_text)

    return {
        "id": db_text.id,
        "content": db_text.content,
        "person": db_text.person,
        "created_at": db_text.created_at
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


# --- Log routes ---

@app.post("/logs")
def create_log(item: LogCreate, db: Session = Depends(get_db)):
    if not item.type or item.count is None:
        raise HTTPException(status_code=400, detail="type and count are required")

    db_log = Log(type=item.type, count=item.count)
    db.add(db_log)
    db.commit()
    db.refresh(db_log)

    return {
        "id": db_log.id,
        "type": db_log.type,
        "count": db_log.count,
        "date": db_log.date
    }


@app.get("/logs")
def get_logs(db: Session = Depends(get_db)):
    logs = db.query(Log).order_by(Log.date.desc()).all()

    return [
        {
            "id": l.id,
            "type": l.type,
            "count": l.count,
            "date": l.date
        }
        for l in logs
    ]


# --- Notes routes ---

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
            "content": n.content
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
        "content": note.content
    }


# --- Task routes ---

@app.post("/tasks")
def create_task(item: TasksCreate, db: Session = Depends(get_db)):
    if not item.description_task or not item.date_task:
        raise HTTPException(
            status_code=400,
            detail="description_task and date_task are required"
        )

    db_task = Tasks(
        description_task=item.description_task,
        isDone=item.isDone if item.isDone is not None else False,
        suggest=item.suggest if item.suggest is not None else False,
        date_task=item.date_task
    )

    db.add(db_task)
    db.commit()
    db.refresh(db_task)

    return {
        "id_task": db_task.id_task,
        "description_task": db_task.description_task,
        "isDone": db_task.isDone,
        "suggest": db_task.suggest,
        "date_task": db_task.date_task
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
            "date_task": t.date_task
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
        "date_task": task.date_task
    }