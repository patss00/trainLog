from fastapi import FastAPI
import json

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
