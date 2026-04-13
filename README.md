# 🚦 Smart City Traffic Control

Sistema inteligente de monitoramento e controle de mobilidade urbana em tempo real.

---

## 🛠️ Tecnologias Utilizadas

<p>
  <img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white"/>
  <img src="https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"/>
  <img src="https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white"/>
  <img src="https://img.shields.io/badge/Python-3776AB?style=for-the-badge&logo=python&logoColor=white"/>
  <img src="https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&logo=html5&logoColor=white"/>
  <img src="https://img.shields.io/badge/CSS3-1572B6?style=for-the-badge&logo=css3&logoColor=white"/>
</p>

---

## 🏙️ Sobre o Projeto

O **Smart Traffic Control** é uma solução completa que atua como um centro de controle de mobilidade urbana.  

Ao invés de semáforos com tempos estáticos, o sistema reage **em tempo real** ao volume de carros e às condições climáticas, tomando decisões inteligentes para evitar congestionamentos.

O sistema combina:
- 🔧 Backend robusto com cálculos geoespaciais  
- 📡 Simulador IoT (Hardware)  
- 📊 Dashboard visual moderno  

Tudo isso permitindo o monitoramento e controle da cidade através de uma interface interativa.

---

## ✨ Funcionalidades Principais

### 🧠 Inteligência do Sistema (Backend & IoT)

- 🚦 **Onda Verde Automática**  
  Algoritmo geoespacial que identifica cruzamentos próximos e sincroniza os semáforos para verde, desafogando vias principais.

- 🌦️ **Simulador de Sensores IoT**  
  Script em Python que consome a API do OpenWeather para injetar dados reais de clima e simula a contagem de veículos ao vivo.

- 🗄️ **Auto-População de Dados**  
  O banco de dados se configura automaticamente com cidades (ex: São Paulo, Recife, Guarulhos) na inicialização.

---

### 🖥️ Centro de Controle (Dashboard Frontend)

- 🗺️ **Mapa Interativo (Leaflet)**  
  Visualização em tempo real de cruzamentos com status dinâmicos:  
  `Normal | Atenção | Congestionado`

- 🌧️ **Clima Dinâmico e Alertas**  
  Detecção em tempo real de:
  - Chuva  
  - Risco Hídrico  
  - Neblina  

- 📊 **Métricas ao Vivo**  
  Painel com cruzamentos monitorados e alertas ativos via WebSocket.

- 🎛️ **Controle Manual (Override)**  
  Possibilidade de abrir/fechar sinais específicos diretamente pela interface web.

---

## 🚀 Objetivo

Demonstrar como tecnologias modernas podem ser aplicadas para criar soluções inteligentes de mobilidade urbana, melhorando o fluxo de trânsito e reduzindo congestionamentos.

---

## 📌 Possíveis Melhorias Futuras

- Integração com dados reais de trânsito (Google Maps, Waze API)
- Machine Learning para previsão de tráfego
- Aplicativo mobile para monitoramento
- Integração com câmeras e visão computacional

---

## 📄 Licença

Este projeto é de uso educacional e demonstrativo.
