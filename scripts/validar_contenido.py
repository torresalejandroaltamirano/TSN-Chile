#!/usr/bin/env python3
"""Validador de candidatos para TSN Chile.

No publica en Supabase. Prepara una cola segura de ofertas/panoramas para revisión.
Reglas: URL de fuente obligatoria, título/cargo obligatorio, fecha vigente y
deduplicación por URL de fuente.
"""
import json
import sys
from datetime import date, datetime, timezone
from pathlib import Path

def clean(v):
    return str(v or "").strip()

def valid_url(v):
    return v.startswith("https://") or v.startswith("http://")

def load(path):
    p=Path(path)
    if not p.exists():
        return []
    data=json.loads(p.read_text(encoding="utf-8"))
    return data if isinstance(data,list) else []

def validate(items, kind):
    out=[]; seen=set()
    today=date.today()
    now=datetime.now(timezone.utc)
    for x in items:
        url=clean(x.get("url_fuente"))
        title=clean(x.get("cargo" if kind=="oferta" else "titulo"))
        if not title or not valid_url(url) or url in seen:
            continue
        try:
            if kind=="oferta":
                cierre=clean(x.get("fecha_cierre"))
                if cierre and date.fromisoformat(cierre) < today:
                    continue
            else:
                fin=clean(x.get("fecha_fin"))
                if fin:
                    dt=datetime.fromisoformat(fin.replace("Z","+00:00"))
                    if dt.tzinfo is None: dt=dt.replace(tzinfo=timezone.utc)
                    if dt < now:
                        continue
        except ValueError:
            continue
        seen.add(url)
        out.append(x)
    return out

if __name__=="__main__":
    ofertas=validate(load("data/candidatos-ofertas.json"),"oferta")
    panoramas=validate(load("data/candidatos-panoramas.json"),"panorama")
    Path("data").mkdir(exist_ok=True)
    Path("data/cola-verificada.json").write_text(
        json.dumps({"ofertas":ofertas,"panoramas":panoramas},ensure_ascii=False,indent=2)+"\n",
        encoding="utf-8")
    print(f"Ofertas candidatas válidas: {len(ofertas)}")
    print(f"Panoramas candidatos válidos: {len(panoramas)}")
