// utils/storageUtils.ts
export const STORAGE_VERSION = "2";

export const generateDeviceId = () => `${Date.now()}-${Math.random().toString(36).substr(2, 9)}`;

export const getOrCreateDeviceId = () => {
    let deviceId = localStorage.getItem("deviceId");
    if (!deviceId) {
        deviceId = generateDeviceId();
        localStorage.setItem("deviceId", deviceId);
    }
    return deviceId;
};

export const checkStorageVersion = () => {
    const clientVersion = localStorage.getItem("storageVersion");
    if (clientVersion !== STORAGE_VERSION) {
        console.log("스토리지 버전 변경 감지, 초기화 진행");
        localStorage.clear();
        localStorage.setItem("storageVersion", STORAGE_VERSION);
    }
};