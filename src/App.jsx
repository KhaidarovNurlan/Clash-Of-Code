import { Routes, Route } from "react-router-dom";
import Layout from "./pages/Layout";
import Home from "./pages/Home";
import Login from "./pages/user/Login";
import Register from "./pages/user/Register";
import Dashboard from "./pages/user/Dashboard";
import Courses from "./pages/courses/Courses";
import CourseDetail from "./pages/courses/CourseDetail";
import Tournaments from "./pages/tournaments/Tournaments";
import TournamentDetail from "./pages/tournaments/TournamentDetail";
import CreateCourse from "./pages/courses/CreateCourse";
import CreateTournament from "./pages/tournaments/CreateTournament";
import NotFound from "./pages/NotFound";
import ProtectedRoute from "./routes/ProtectedRoute";
import TeacherRoute from "./routes/TeacherRoute";
import { useAuth } from "./contexts/AuthContext";
import ForgotPassword from "./pages/user/ForgotPassword";
import ResetPassword from "./pages/user/ResetPassword";

function App() {
  const { loading } = useAuth();

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="animate-pulse flex flex-col items-center">
          <div className="text-4xl font-bold mb-4 text-blue-500">
            Clash Of Code
          </div>
          <div className="text-slate-400">
            Loading awesome coding challenges...
          </div>
        </div>
      </div>
    );
  }

  return (
    <Routes>
      <Route path="/" element={<Layout />}>
        <Route index element={<Home />} />
        <Route path="login" element={<Login />} />
        <Route path="register" element={<Register />} />
        <Route path="/forgot-password" element={<ForgotPassword />} />
        <Route path="/reset-password" element={<ResetPassword />} />

        <Route element={<ProtectedRoute />}>
          <Route path="dashboard" element={<Dashboard />} />
          <Route path="courses" element={<Courses />} />
          <Route path="courses/:id" element={<CourseDetail />} />
          <Route path="tournaments" element={<Tournaments />} />
          <Route path="tournaments/:id" element={<TournamentDetail />} />

          <Route element={<TeacherRoute />}>
            <Route path="create-course" element={<CreateCourse />} />
            <Route path="create-tournament" element={<CreateTournament />} />
          </Route>
        </Route>

        <Route path="*" element={<NotFound />} />
      </Route>
    </Routes>
  );
}

export default App;
