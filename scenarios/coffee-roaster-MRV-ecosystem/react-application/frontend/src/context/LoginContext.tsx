import React, {
  createContext,
  useContext,
  useState,
  useEffect,
  ReactNode,
} from "react";

type LoginStatus = "nobody" | "owner" | "inspector";
type LoginContextType = {
  loginStatus: LoginStatus;
  setLoginStatus: React.Dispatch<React.SetStateAction<LoginStatus>>;
};

const LoginContext = createContext<LoginContextType | undefined>(undefined);

export const useLogin = (): LoginContextType => {
  const context = useContext(LoginContext);
  if (context === undefined) {
    throw new Error("useLogin must be used within a LoginProvider");
  }
  return context;
};

interface LoginProviderProps {
  children: ReactNode;
}

export const LoginProvider: React.FC<LoginProviderProps> = ({ children }) => {
  const [loginStatus, setLoginStatus] = useState<LoginStatus>(
    (localStorage.getItem("loginStatus") as LoginStatus) || "nobody"
  );
  const [socket, setSocket] = useState<WebSocket | null>(null);

  useEffect(() => {
    const connectWebSocket = () => {
      if (socket) {
        socket.close();
      }
      const newSocket = new WebSocket("ws://127.0.0.1:1880/ws/loginstatus");

      newSocket.onmessage = (event) => {
        const data = JSON.parse(event.data);
        localStorage.setItem("loginStatus", data.loggedIn);
        setLoginStatus(data.loggedIn as LoginStatus);
      };

      newSocket.onopen = () => {
        console.log("WebSocket Connected");
      };

      newSocket.onerror = () => {
        console.log("WebSocket Error, retrying...");
        setTimeout(connectWebSocket, 5000);
      };

      setSocket(newSocket);
    };

    connectWebSocket();

    return () => {
      if (socket) {
        socket.close();
      }
    };
  }, []);

  return (
    <LoginContext.Provider value={{ loginStatus, setLoginStatus }}>
      {children}
    </LoginContext.Provider>
  );
};
