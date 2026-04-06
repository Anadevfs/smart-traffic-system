import requests
import random
import time

# --- CONFIGURAÇÕES ---
JAVA_URL = "http://localhost:8080/api/traffic"
API_KEY = "API_AQUI" # OpenWeatherMap
CIDADE = "Guarulhos" 
IDS_CRUZAMENTOS = [1, 2, 3]

def get_real_weather():
    """Consulta o clima real e traduz para as regras do nosso Java"""
    try:
        url = f"http://api.openweathermap.org/data/2.5/weather?q={CIDADE}&appid={API_KEY}&units=metric"
        response = requests.get(url)
        
        if response.status_code != 200:
            print(f"⚠️ Erro na API (Status {response.status_code}). Verifique se a chave já ativou.")
            return "NORMAL"

        data = response.json()
        main_weather = data['weather'][0]['main'] 
        temp = data['main']['temp']
        
        print(f"🌍 [API] {CIDADE}: Status = {main_weather} | Temp = {temp}°C")

        if main_weather in ["Rain", "Drizzle", "Thunderstorm", "Mist", "Fog"]:
            return "HEAVY_RAIN"
        
        return "NORMAL"
        
    except Exception as e:
        print(f"❌ Falha crítica ao buscar clima: {e}")
        return "NORMAL"

print(f"🚀 Simulador rodando! Monitorando {CIDADE}...")
print("Pressione Ctrl+C para encerrar.\n")

while True:
    # Busca o clima real do momento
    clima_da_rua = get_real_weather()

    for cid in IDS_CRUZAMENTOS:
        # Sorteia a quantidade de carros (entre 0 e 48)
        carros = random.randint(0, 48)

        # Lógica de Acidente 
        if random.random() < 0.15:
            condicao_final = "ACCIDENT"
        else:
            condicao_final = clima_da_rua

        # Prepara o pacote JSON para o Java
        payload = {
            "carCount": carros,
            "condition": condicao_final
        }

        try:
            # Envia para o nosso Backend Java
            res = requests.post(f"{JAVA_URL}/{cid}/update", json=payload)
            
            if res.status_code == 200:
                print(f"✅ Cruzamento {cid}: {carros} carros | Status: {condicao_final}")
            else:
                print(f"⚠️ Java respondeu com erro {res.status_code}")
                
        except Exception as e:
            print(f"❌ Erro de conexão: O Java está desligado?")

    print("-" * 40)
    # Aguarda 10 segundos para a próxima atualização
    time.sleep(10)