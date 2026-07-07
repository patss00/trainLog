import os
import json
from pathlib import Path

import requests
from dotenv import load_dotenv
from sqlalchemy import null


# ============================================================
# LOCAL SETTINGS
# ============================================================

BASE_DIR = Path(__file__).resolve().parent
ENV_PATH = BASE_DIR / ".env"

load_dotenv(ENV_PATH)

OCR_SPACE_API_KEY = os.getenv("OCR_SPACE_API_KEY")

IMAGE_PATH = Path(r"C:\Users\PatriciaSilva\Downloads\1.jpg")


# ============================================================
# OCR LOGIC
# ============================================================

def process_schedule_image(image_path: Path):
    if not OCR_SPACE_API_KEY:
        raise RuntimeError("OCR_SPACE_API_KEY is not set in .env")

    if not image_path.exists():
        raise FileNotFoundError(f"Image not found: {image_path}")

    contents = image_path.read_bytes()

    if not contents:
        raise RuntimeError(f"{image_path.name} is empty")

    ocr_response = requests.post(
        "https://api.ocr.space/parse/image",
        headers={
            "apikey": OCR_SPACE_API_KEY,
        },
        files={
            "file": (
                image_path.name,
                contents,
                "image/jpeg",
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
        raise RuntimeError(f"OCR request failed: {ocr_response.text}")

    ocr_data = ocr_response.json()

    if ocr_data.get("IsErroredOnProcessing"):
        raise RuntimeError(
            ocr_data.get("ErrorMessage", "OCR processing failed")
        )

    parsed_results = ocr_data.get("ParsedResults", [])

    extracted_text = ""

    if parsed_results:
        extracted_text = parsed_results[0].get("ParsedText", "")

    return {
        "filename": image_path.name,
        "text": extracted_text,
    }




# ============================================================
# RUN LOCAL TEST
# ============================================================

def main():
    processed_file = process_schedule_image(IMAGE_PATH)

    raw_text = processed_file["text"]
    linesRaw = raw_text.splitlines()

    cleaned_lines = []

    for line in linesRaw:
        clean_line = line.strip()

        if clean_line:
            cleaned_lines.append(clean_line)
    
    days = ['mon', 'tue', 'wed', 'thu', 'fri', 'sat', 'sun']
    months = ['jan', 'feb', 'mar', 'apr', 'may', 'jun', 'jul', 'aug', 'sep', 'oct', 'nov', 'dec']
    lines = [i for i in cleaned_lines if i != 'Schedule' and i != 'Planned shifts' and i != 'Worked shifts' and i != 'Upcoming' and i != 'Pay Period' and i != 'Upcoming months' and i != 'PORT CHIADO' and i != 'This week' and 'Select' not in i and i != 'Info' and i != 'My shifts' and i != 'Shift Exchange' and i != 'My Absence' and i != 'Menu' and i != '|||']

    
    events = []
    
    for line in lines:
        if line.lower() in days:
            eventDate = "2026-"
            monthInt = months.index(lines[lines.index(line) + 2].lower()) + 1
            day = lines[lines.index(line) + 1]
            month = ""
            if monthInt < 10:
                month = "0" + str(monthInt)
            else:
                month = str(monthInt)
            eventDate += month + "-" + day
            description = ""
            startTime = ""
            endTime = ""
            if 'Día libre' in lines[lines.index(line) + 3]:
                description = "folga"
                startTime = "00:00"
                endTime = "00:00"
            else:
                description = "trabalho"
                startTime = lines[lines.index(line) + 3][:5]
                endTime = lines[lines.index(line) + 3][8:13]
            events.append({
                "event_id": null,
                "date": eventDate,
                "description": description,
                "start_time": startTime,
                "end_time": endTime,
            })



    


if __name__ == "__main__":
    main()