export const environment = {
    componentUrl: (window as { [key: string]: any })["env"]["componentUrl"] || "http://0.0.0.0:8080",
    poolTime: (window as { [key: string]: any })["env"]["poolTime"] || 1500
};
