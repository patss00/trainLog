import mimetypes
import os
from fastapi import Depends, FastAPI, HTTPException
import json
from pydantic import BaseModel
from sqlalchemy import create_engine, Column, Integer, String, DateTime
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker, Session
from datetime import datetime
from typing import Optional
from fastapi.responses import FileResponse
from fastapi.staticfiles import StaticFiles

app = FastAPI()

# Load stations.json once at startup
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

# Mount a "static" folder to serve images
mimetypes.add_type("image/jpg", ".jpg")

static_dir = os.path.join(os.getcwd(), "static")
app.mount("/static", StaticFiles(directory=static_dir), name="static")

@app.get("/pic")
def get_pic():
    # Put your image in the "static" folder
    file_path = os.path.join(static_dir, "pic.jpg")
    if not os.path.exists(file_path):
        return {"error": f"File not found: {file_path}"}
    # Return the image file directly
    return FileResponse(file_path, media_type="image/jpg")


# --- DB Setup ---
DATABASE_URL = os.getenv("DATABASE_URL", "postgresql://trainlog_db_user:i6e4y2wOTyH8NpXNwSIXjH8l20Q62Yx3@dpg-d33b383uibrs73afdapg-a.frankfurt-postgres.render.com:5432/trainlog_db")

engine = create_engine(DATABASE_URL)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
Base = declarative_base()

# --- Model ---
class Text(Base):
    __tablename__ = "texts"

    id = Column(Integer, primary_key=True, index=True)
    content = Column(String, nullable=False)
    person = Column(String, nullable=False)
    created_at = Column(DateTime, default=datetime.now)

class Log(Base):
    __tablename__ = "logs"

    id = Column(String, primary_key=True)
    count = Column(Integer, nullable=False)
    date = Column(DateTime, default=datetime.now)

Base.metadata.create_all(bind=engine)

# Dependency
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
    id: Optional[str] = None
    count: Optional[int] = None

# --- Routes ---
@app.post("/texts")
def create_text(item: TextCreate, db: Session = Depends(get_db)):
    db_text = Text(content=item.content, person=item.person)
    db.add(db_text)
    db.commit()
    db.refresh(db_text)
    return {"id": db_text.id, "content": db_text.content, "person": db_text.person, "created_at": db_text.created_at}

@app.post("/logs")
def create_log(db: Session = Depends(get_db)):
    logs = db.query(Log).all()
    return [
        {
            "id": l.id,
            "count": l.count,
            "date": l.date,
        }
        for l in logs
    ]

@app.get("/logs")
def get_logs(db: Session = Depends(get_db)):
    logs = db.query(Text).all()
    return [
        {
            "id": t.id,
            "count": t.count,
            "date": t.date,
        }
        for t in logs
    ]

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

#@app.post("/texts")
#def create_text(item: TextCreate, db: Session = Depends(get_db)):
#    db_text = Text(content=item.content, person=item.person)  # include person now
 #   db.add(db_text)
  #  db.commit()
   # db.refresh(db_text)
    #return {"id": db_text.id, "content": db_text.content, "person": db_text.person, "created_at": db_text.created_at}

@app.delete("/texts/{text_id}")
def delete_text(text_id: int, db: Session = Depends(get_db)):
    db_text = db.query(Text).filter(Text.id == text_id).first()
    if not db_text:
        raise HTTPException(status_code=404, detail="Text not found")
    db.delete(db_text)
    db.commit()
    return {"message": f"Text with id {text_id} has been deleted."}
