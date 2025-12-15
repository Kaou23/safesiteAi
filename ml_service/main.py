"""
SafeSite AI - ML Service
FastAPI microservice for construction site risk analysis.
"""

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from typing import List, Optional

app = FastAPI(
    title="SafeSite AI - ML Service",
    description="Microservice IA pour l'analyse des risques sur chantier",
    version="1.0.0"
)

# CORS configuration for Flutter frontend
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


class SiteData(BaseModel):
    """Input data for risk prediction."""
    temperature: Optional[float] = 25.0
    humidity: Optional[float] = 50.0
    epi_compliance: float  # Equipment Protection Individual compliance (%)
    fatigue: float  # Fatigue level (1-10)
    working_hours: Optional[float] = 8.0
    workers_count: Optional[int] = 10
    hazardous_materials: Optional[bool] = False
    weather_conditions: Optional[str] = "normal"


class RiskPrediction(BaseModel):
    """Output risk prediction result."""
    riskScore: int
    riskLevel: str
    recommendations: List[str]


@app.get("/")
async def root():
    """Health check endpoint."""
    return {"status": "healthy", "service": "SafeSite AI ML Service"}


@app.get("/health")
async def health_check():
    """Health check for Docker."""
    return {"status": "healthy"}


@app.post("/predict", response_model=RiskPrediction)
async def predict_risk(data: SiteData) -> RiskPrediction:
    """
    Predict construction site risk based on input parameters.
    
    Risk Rules:
    - HIGH risk if epi_compliance < 85% OR fatigue > 6
    - LOW risk otherwise
    """
    recommendations = []
    risk_score = 0
    
    # EPI Compliance check
    if data.epi_compliance < 85:
        risk_score += 40
        recommendations.append(
            f"⚠️ Conformité EPI insuffisante ({data.epi_compliance}%). "
            "Vérifier le port des équipements de protection."
        )
    
    # Fatigue check
    if data.fatigue > 6:
        risk_score += 35
        recommendations.append(
            f"😴 Niveau de fatigue élevé ({data.fatigue}/10). "
            "Prévoir des pauses régulières et rotation des équipes."
        )
    
    # Temperature check
    if data.temperature and data.temperature > 35:
        risk_score += 15
        recommendations.append(
            f"🌡️ Température élevée ({data.temperature}°C). "
            "Hydratation obligatoire et pauses à l'ombre."
        )
    elif data.temperature and data.temperature < 5:
        risk_score += 10
        recommendations.append(
            f"❄️ Température basse ({data.temperature}°C). "
            "Équipements chauds et vigilance accrue."
        )
    
    # Working hours check
    if data.working_hours and data.working_hours > 10:
        risk_score += 10
        recommendations.append(
            "⏰ Heures de travail excessives. Limiter à 10h maximum."
        )
    
    # Hazardous materials check
    if data.hazardous_materials:
        risk_score += 15
        recommendations.append(
            "☢️ Présence de matériaux dangereux. Protocoles spéciaux requis."
        )
    
    # Determine risk level
    if data.epi_compliance < 85 or data.fatigue > 6:
        risk_level = "ÉLEVÉ"
        risk_score = max(risk_score, 70)
    elif risk_score >= 50:
        risk_level = "MOYEN"
    else:
        risk_level = "FAIBLE"
        if not recommendations:
            recommendations.append("✅ Conditions de travail optimales. Continuer les bonnes pratiques.")
    
    # Cap risk score at 100
    risk_score = min(risk_score, 100)
    
    return RiskPrediction(
        riskScore=risk_score,
        riskLevel=risk_level,
        recommendations=recommendations
    )


if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
