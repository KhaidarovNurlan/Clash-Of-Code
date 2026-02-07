import { createContext, useContext, useState, useEffect } from "react";
import axios from "axios";

const AuthContext = createContext();

export const useAuth = () => useContext(AuthContext);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const checkLoggedIn = async () => {
      const token = localStorage.getItem("token");

      if (token) {
        try {
          axios.defaults.headers.common["Authorization"] = `Bearer ${token}`;

          const response = await axios.get(`http://localhost:8080/auth/me`);
          setUser(response.data);
        } catch (error) {
          console.error("Token verification failed", error);
          localStorage.removeItem("token");
          delete axios.defaults.headers.common["Authorization"];
        }
      }

      setLoading(false);
    };

    checkLoggedIn();
  }, []);

  const register = async (userData) => {
    const response = await axios.post(`http://localhost:8080/auth/register`, userData);

    if (response.data.token) {
      localStorage.setItem("token", response.data.token);
      axios.defaults.headers.common[
        "Authorization"
      ] = `Bearer ${response.data.token}`;
      setUser(response.data.user);
    }

    return response.data;
  };

  const login = async (credentials) => {
    try {
      const response = await axios.post(`http://localhost:8080/auth/login`, credentials);

      if (response.data.token) {
        localStorage.setItem("token", response.data.token);
        axios.defaults.headers.common["Authorization"] = `Bearer ${response.data.token}`;
        setUser(response.data.user);
        return response.data;
      } else {
        throw new Error(response.data.message || "Invalid credentials");
      }
    } catch (error) {
      throw error;
    }
  };

  const logout = () => {
    localStorage.removeItem("token");
    delete axios.defaults.headers.common["Authorization"];
    setUser(null);
  };

  const isTeacher = user?.role === "teacher";

  const updateProfile = async (userData) => {
    const response = await axios.put(`http://localhost:8080/users/profile`, userData);
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
