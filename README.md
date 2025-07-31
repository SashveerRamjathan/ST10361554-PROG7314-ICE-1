# 🐍 Snake Game Android App

Welcome to the **Snake Game Android App**! 🚀  
A modern twist on the classic snake game, built natively in Kotlin for Android. Challenge yourself, climb the leaderboard, and see your skills in action!

---

## 📚 Table of Contents

- [✨ Features](#features)
- [⚡ Getting Started](#getting-started)
- [🛠️ Tech Stack](#tech-stack)
- [📁 Project Structure](#project-structure)
- [🤓 Usage](#usage)
- [👤 Author & Module Info](#author--module-info)
- [👥 Contributing](#contributing)
- [🙏 Acknowledgements](#acknowledgements)
- [📄 License](#license)

---

## ✨ Features

- 👆 **Swipe Controls:** Change snake direction with swipe gestures for intuitive gameplay.
- 💀 **Game Over Screen:** Displays your score when you lose, and saves it to Firestore for persistence.
- 🔑 **Authentication:** Login and register securely with email and password.
- 🏆 **Leaderboard:** See the top 10 scores, complete with usernames — all pulled live from Firestore.
- 🏅 **Personal Scores:** Your own scores are listed.
- 📱 **Responsive UI:** Designed for a smooth experience across Android devices.

---

## ⚡ Getting Started

Get the Snake Game running on your Android device in a few easy steps:

### 🧰 Prerequisites

- Android Studio (Arctic Fox or newer recommended)
- Kotlin >= 1.8
- A connected Android device or emulator
- Firebase account & Firestore set up

### 🏗️ Installation

1. **Clone the repository:**
   ```bash
   git clone https://github.com/SashveerRamjathan/ST10361554-PROG7314-ICE-1.git
   cd ST10361554-PROG7314-ICE-1
   ```

2. **Open in Android Studio:**
   - File > Open > Select the cloned folder.

3. **Set up Firebase:**
   - Create a Firebase project.
   - Enable Firestore and Email/Password authentication.
   - Download `google-services.json` and place it in your app's `/app` directory.

4. **Build & Run:**
   - Click the ▶️ Run button in Android Studio.
   - Install on your device or launch the emulator.

---

## 🛠️ Tech Stack

- **Language:** Kotlin
- **Framework:** Android SDK
- **Database:** Firebase Firestore
- **Authentication:** Firebase Auth
- **Other:** Material Design

---

## 📁 Project Structure

```
ST10361554-PROG7314-ICE-1/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/             # Kotlin source code
│   │   │   ├── res/              # Layouts, drawables
│   │   │   └── AndroidManifest.xml
│   └── google-services.json      # Firebase config
├── build.gradle
├── README.md
└── .gitignore
```

---

## 🤓 Usage

- **Swipe** to navigate your snake through the grid.
- **Login/Register** to track your scores.
- **Game Over?** View your score and see it saved instantly.
- **Check the Leaderboard** to compete with others and see your place!

---

## 👤 Author & Module Info

- **Name:** Sashveer Lakhan Ramjathan  
- **Student Number:** ST10361554  
- **Group:** 2  
- **Module:** PROG7314  
- **Assessment:** ICE Task 1  

---

## 👥 Contributing

Contributions are welcome!  
Open issues for suggestions or bugs, and submit pull requests for improvements.

---

## 🙏 Acknowledgements

Inspired by classic snake games and powered by Android & Firebase.

---
