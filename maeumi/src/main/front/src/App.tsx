import { BrowserRouter, Routes, Route } from 'react-router-dom';
import "./styles/global.css";
import ChatPage from "./pages/ChatPage";
import EmotionAnalysis from "./pages/EmotionAnalysis";

function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<ChatPage />} />
                <Route path="/sentiment" element={<EmotionAnalysis />} />
            </Routes>
        </BrowserRouter>
    );
}

export default App;
