import mimetypes
import os
from fastapi import Body, Depends, FastAPI, HTTPException, Request
import json
from pydantic import BaseModel
from sqlalchemy import create_engine, Column, Integer, String, DateTime
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker, Session
from datetime import datetime
from typing import Optional
from fastapi.responses import FileResponse
from fastapi.staticfiles import StaticFiles
from contextlib import asynccontextmanager

# --- FastAPI app with lifespan ---
@asynccontextmanager
async def lifespan(app: FastAPI):
    # Ensure single note row exists at startup
    db = SessionLocal()
    if db.query(Notes).first() is None:
        db.add(Notes(content=""))  # create default empty note
        db.commit()
    db.close()
    yield
    # Shutdown code can go here if needed

app = FastAPI(lifespan=lifespan)

# --- Load static JSON data ---
with open("stations.json", "r", encoding="utf-8") as f:
    data = json.load(f)

@app.get("/")
def root():
    return {"message": "Train API is running"}

@app.get("/lines")
def get_lines():
    return data["lines"]

@app.get("/allStations")
def get_all_stations():
    return data["allStations"]

@app.get("/cutes")
def get_all_cutes():
    return data["cutes"]

@app.get("/screen")
def get_screen():
    return data["screen"]

@app.get("/lines/{line_id}")
def get_line(line_id: str):
    for line in data["lines"]:
        if line["id"] == line_id:
            return line
    return {"error": "Line not found"}

@app.get("/stations/{station_name}")
def get_station(station_name: str):
    for line in data["lines"]:
        for station in line["stations"]:
            if station["name"].lower() == station_name.lower():
                return station
    return {"error": "Station not found"}

# --- Static files ---
mimetypes.add_type("image/jpg", ".jpg")
static_dir = os.path.join(os.getcwd(), "static")
app.mount("/static", StaticFiles(directory=static_dir), name="static")

@app.get("/pic")
def get_pic():
    file_path = os.path.join(static_dir, "pic.jpg")
    if not os.path.exists(file_path):
        return {"error": f"File not found: {file_path}"}
    return FileResponse(file_path, media_type="image/jpg")

# --- Database setup ---
DATABASE_URL = os.getenv(
    "DATABASE_URL",
    "postgresql://trainlog_db_user:i6e4y2wOTyH8NpXNwSIXjH8l20Q62Yx3@dpg-d33b383uibrs73afdapg-a.frankfurt-postgres.render.com:5432/trainlog_db"
)

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
    id = Column(Integer, primary_key=True, autoincrement=True)
    type = Column(String, primary_key=True)
    count = Column(Integer, nullable=False)
    date = Column(DateTime, default=datetime.now)

class Notes(Base):
    __tablename__ = "notes"
    id = Column(Integer, primary_key=True, autoincrement=True)
    content = Column(String, nullable=False)

Base.metadata.create_all(bind=engine)

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
    log_type: Optional[str] = None
    count: Optional[int] = None

class NoteUpdate(BaseModel):
    content: str

class NoteOut(BaseModel):
    content: str

# --- Routes ---
@app.post("/texts")
def create_text(item: TextCreate, db: Session = Depends(get_db)):
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

@app.post("/logs")
def create_log(item: LogCreate, db: Session = Depends(get_db)):
    db_log = Log(log_type=item.log_type, count=item.count)
    db.add(db_log)
    db.commit()
    db.refresh(db_log)
    return {"id": db_log.id, "log_type": db_log.log_type, "count": db_log.count, "date": db_log.date}

@app.get("/logs")
def get_logs(db: Session = Depends(get_db)):
    logs = db.query(Log).all()
    return [{"id": l.id, "log_type": l.log_type, "count": l.count, "date": l.date} for l in logs]

@app.get("/texts")
def get_texts(db: Session = Depends(get_db)):
    texts = db.query(Text).all()
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

# --- Notes routes ---

# --- PUT route ---

@app.put("/note", response_model=NoteOut)
def update_note(
    body: dict = Body(...),  # accept whatever FlutterFlow sends
    db: Session = Depends(get_db)
):
    # Try to extract "content" from the incoming JSON
    content = body.get("content", "")

    note = db.query(Notes).first()
    if not note:
        # If no row exists, insert one
        note = Notes(content=content)
        db.add(note)
    else:
        # Always update the first row
        note.content = content
    db.commit()
    db.refresh(note)

    return {"content": note.content}


# --- GET route ---
@app.get("/note", response_model=NoteOut)
def get_note(db: Session = Depends(get_db)):
    note = db.query(Notes).first()
    if not note:
        # Guarantee a row always exists
        note = Notes(content="")
        db.add(note)
        db.commit()
        db.refresh(note)
    return {"content": note.content}

@app.get("/debug/notes")
def debug_notes(db: Session = Depends(get_db)):
    notes = db.query(Notes).all()
    return [{"id": n.id, "content": n.content} for n in notes]


@app.get("/debug/reset_note")
def reset_note(db: Session = Depends(get_db)):
    note = db.query(Notes).first()
    if note:
        note.content = ""  # reset to empty string
    else:
        note = Notes(content="")
        db.add(note)
    db.commit()
    db.refresh(note)
    return {"id": note.id, "content": note.content}
