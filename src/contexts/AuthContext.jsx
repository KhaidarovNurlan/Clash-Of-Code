import { createContext, useContext, useState, useEffect } from "react";
import api from "../axios";

const AuthContext = createContext();

export const useAuth = () => useContext(AuthContext);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const checkLoggedIn = async () => {
      const token = localStorage.getItem("token");
      if (!token) { setLoading(false); return; }
      try {
        const response = await api.get("/auth/me");
        setUser(response.data);
      } catch (error) {
        console.error("Token verification failed", error);
      } finally {
        setLoading(false);
      }
    };
    checkLoggedIn();
  }, []);

  const register = async (userData) => {
    const response = await api.post("/auth/register", userData);
    if (response.data.token) {
      localStorage.setItem("token", response.data.token);
      setUser(response.data.user);
    }
    return response.data;
  };

  const login = async (credentials) => {
    try {
      const response = await api.post("/auth/login", credentials);
      if (response.data.token) {
        localStorage.setItem("token", response.data.token);
        setUser(response.data.user);
        return response.data;
      }
    } catch (error) {
      throw error;
    }
  };

  const logout = () => {
    localStorage.removeItem("token");
    setUser(null);
  };

  const isTeacher = user?.role === "teacher";

  const updateProfile = async (userData) => {
    const response = await api.put(`/users/profile`, userData);
    setUser(response.data);
    return response.data;
  };

  const value = {
    user,
    loading,
    register,
    login,
    logout,
    isTeacher,
    updateProfile,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};
