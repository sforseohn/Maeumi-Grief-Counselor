async function analysisEmotion() {
    const promptText = document.getElementById('promptText').value;
    const testSentence = document.getElementById('testSentence').value;
    const responseBox = document.getElementById('responseBox');

    try {
        const response = await fetch('/api/emotion-analysis', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ promptText: promptText, testSentence: testSentence })
        });

        if (!response.ok) {
            const errorDetails = await response.text();
            throw new Error(`HTTP ${response.status}: ${response.statusText} - ${errorDetails}`);
        }

        const data = await response.json();
        responseBox.textContent = data.analysis;
    } catch (error) {
        responseBox.textContent = 'Error: ' + error.message;
        console.error('Detailed error:', error);
    }
}

