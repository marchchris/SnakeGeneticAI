# 🐍 Snake AI  

 A Neural Network That Learns to Play Snake Using a Genetic Algorithm

<p align="center">
  <img src="imgs/snakeGif.gif" alt="Snake AI in action">
</p>

## 🚀 Key Features  

### 🧠 **24-Sensor Vision System**  
- The snake perceives its environment through **8 directional sensors**:  
  **N, S, W, E, NE, SE, SW, NW**  
- Each sensor detects:  
  - Distance to the wall  
  - Presence of food  
  - Its own tail  
- **Total Inputs:** `3 x 8 = 24 sensors`

<p align="center">
  <img src="imgs/snakeSensors.PNG" width="300px" alt="Snake Sensor System">
</p>

### 🤖 **Neural Network Decision Making**  
- The game state is processed through a **feedforward neural network**  
  **Architecture:** `24 (input) → 16 → 16 → 4 (output)`  
- The output with the highest activation determines the snake’s move  

<p align="center">
  <img src="imgs/neuralNetworkArchitecture.PNG" width="600px" alt="Neural Network Architecture">
</p>

### 🧬 **Self-Learning via Genetic Algorithm**  
- **Evolution Process:**
  - The **top 10%** of snakes survive to the next generation  
  - The remaining **90%** are generated using:
    - **Roulette wheel selection** (choosing parents based on fitness)  
    - **Crossover** (combining parent weights)  
    - **Mutation** (random weight adjustments)  
  - Each generation consists of **1,000 snakes**  

<p align="center">
  <img src="imgs/snakeGeneticAlg.png" width="400px" alt="Genetic Algorithm Process">
</p>

### 📈 **Fitness Function**  
The AI is rewarded for **both survival and apple collection**:  
- **Longer survival** earns a linear reward  
- **Eating apples** gives an **exponential** bonus  

$$
\text{fitness} = \text{totalSteps} \cdot \text{score}^2
$$

## 📥 Download & Run  

🔗 **[Download Here](https://github.com/marchchris/SnakeGeneticAI/tree/fc39bd07b3be70b2887ffb65be67748dedfd9712/src)**  

Clone the repo and run it locally to watch the AI evolve over time! 🚀  

## 🎥 Related  

📺 **Watch the full explanation & demo on YouTube:**  
[▶️ Neural Network Snake AI Video](https://youtu.be/iqisOpNVir8?si=l0bohj50Q8YSrfJT)  

