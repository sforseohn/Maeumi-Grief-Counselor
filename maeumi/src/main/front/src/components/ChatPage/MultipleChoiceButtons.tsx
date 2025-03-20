import React from "react";
import "../../styles/global.css";
import "../../styles/MultipleChoiceButtons.css";

interface MultipleChoiceButtonsProps {
    options: string[];
    onSelect: (idx: string, option: string) => void;
}

export default function MultipleChoiceButtons({ options, onSelect }: { options: string[], onSelect: (key: string) => void }) {
    return (
        <div className="choice-buttons">
            {options.map((value, index) => (
                <button key={index} className="choice-button" onClick={() => onSelect(String(index))}>
                    {value}
                </button>
            ))}
        </div>
    );
}

