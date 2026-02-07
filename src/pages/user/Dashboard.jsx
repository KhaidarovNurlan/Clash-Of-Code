import { useState, useEffect } from "react";
import { useAuth } from "../../contexts/AuthContext";
import { useToast } from "../../contexts/ToastContext";
import { AlertCircle, Trash2 } from "lucide-react";
import axios from "axios";

const Dashboard = () => {
  const { user, updateProfile } = useAuth();
  const { showToast } = useToast();
  const [isEditing, setIsEditing] = useState(false);
  const [newUsername, setNewUsername] = useState(user?.username || "");
  const [error, setError] = useState("");

  const [courses, setCourses] = useState([]);
  const [tournaments, setTournaments] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (user?.role === "admin") {
      const fetchData = async () => {
        setLoading(true);
        try {
          const [coursesRes, tournamentsRes] = await Promise.all([
            axios.get("http://localhost:8080/courses"),
            axios.get("http://localhost:8080/tournaments"),
          ]);
          setCourses(Array.isArray(coursesRes.data) ? coursesRes.data : coursesRes.data.courses || []);
          setTournaments(Array.isArray(tournamentsRes.data) ? tournamentsRes.data : tournamentsRes.data.tournaments || []);
        } catch (err) {
          console.error("Error loading admin data:", err);
        } finally {
          setLoading(false);
        }
      };
      fetchData();
    }
  }, [user]);

  const handleDelete = async (type, id) => {
    if (!window.confirm(`Are you sure you want to delete this ${type}?`)) return;

    try {
      await axios.delete(`http://localhost:8080/admin/${type}/${id}`, {
        headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
      });
      showToast(`${type.charAt(0).toUpperCase() + type.slice(1)} deleted successfully`, "success");

      if (type === "course") {
        setCourses((prev) => prev.filter((c) => c.id !== id));
      } else {
        setTournaments((prev) => prev.filter((t) => t.id !== id));
      }
    } catch (err) {
      console.error(`Error deleting ${type}:`, err);
      showToast("Failed to delete " + type, "error");
    }
  };

  const handleUsernameChange = async (e) => {
    e.preventDefault();

    if (!newUsername.trim()) {
      setError("Username cannot be empty");
      return;
    }

    try {
      const response = await axios.put(
        `http://localhost:8080/users/profile`,
        { username: newUsername },
        {
          headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
        }
      );

      await updateProfile(response.data);
      setIsEditing(false);
      showToast("Username updated successfully!", "success");
    } catch (error) {
      console.error("Error updating username:", error);
      setError(error.response?.data?.message || "Failed to update username");
    }
  };

  return (
    <div className="max-w-4xl mx-auto mt-12 mb-12">
      <h1 className="text-3xl font-bold text-white mb-8">Profile</h1>

      <div className="card p-6 space-y-6">
        <div>
          <label className="text-sm text-slate-400">Username</label>
          {isEditing ? (
            <form onSubmit={handleUsernameChange} className="mt-1 space-y-2">
              <input
                type="text"
                value={newUsername}
                onChange={(e) => {
                  setNewUsername(e.target.value);
                  setError("");
                }}
                className="form-input"
                placeholder="Enter new username"
              />
              {error && (
                <p className="text-sm text-red-500 flex items-center">
                  <AlertCircle size={14} className="mr-1" />
                  {error}
                </p>
              )}
              <div className="flex gap-2">
                <button type="submit" className="btn btn-primary">
                  Save
                </button>
                <button
                  type="button"
                  onClick={() => {
                    setIsEditing(false);
                    setNewUsername(user?.username || "");
                    setError("");
                  }}
                  className="btn btn-outline"
                >
                  Cancel
                </button>
              </div>
            </form>
          ) : (
            <div className="flex items-center justify-between mt-1">
              <p className="text-white text-lg">{user?.username}</p>
              <button
                onClick={() => setIsEditing(true)}
                className="btn btn-outline btn-sm"
              >
                Change
              </button>
            </div>
          )}
        </div>

        <div>
          <label className="text-sm text-slate-400">Role</label>
          <p className="text-white text-lg mt-1">{user?.role}</p>
        </div>

        <div>
          <label className="text-sm text-slate-400">Points</label>
          <p className="text-white text-lg mt-1">{user?.points || 0}</p>
        </div>

        <div>
          <label className="text-sm text-slate-400">Member Since</label>
          <p className="text-white text-lg mt-1">
            {new Date(user?.createdAt).toLocaleDateString()}
          </p>
        </div>
      </div>

      {user?.role === "admin" && (
        <div className="card mt-10 p-6 space-y-6 border border-red-500/50 bg-red-500/10">
          <h2 className="text-2xl font-bold text-red-400 flex items-center gap-2">
            🛠 Admin Panel
          </h2>

          {loading ? (
            <p className="text-slate-400">Loading data...</p>
          ) : (
            <>
              <div>
                <h3 className="text-xl text-white mb-2">Courses</h3>
                {courses.length === 0 ? (
                  <p className="text-slate-400 text-sm">No courses found.</p>
                ) : (
                  <ul className="space-y-2">
                    {courses.map((course) => (
                      <li
                        key={course.id}
                        className="flex justify-between items-center bg-slate-800 p-3 rounded"
                      >
                        <span className="text-white">{course.title}</span>
                        <button
                          onClick={() => handleDelete("course", course.id)}
                          className="text-red-500 hover:text-red-700 flex items-center"
                        >
                          <Trash2 size={16} className="mr-1" /> Delete
                        </button>
                      </li>
                    ))}
                  </ul>
                )}
              </div>

              <div>
                <h3 className="text-xl text-white mb-2">Tournaments</h3>
                {tournaments.length === 0 ? (
                  <p className="text-slate-400 text-sm">No tournaments found.</p>
                ) : (
                  <ul className="space-y-2">
                    {tournaments.map((t) => (
                      <li
                        key={t.id}
                        className="flex justify-between items-center bg-slate-800 p-3 rounded"
                      >
                        <span className="text-white">{t.title}</span>
                        <button
                          onClick={() => handleDelete("tournament", t.id)}
                          className="text-red-500 hover:text-red-700 flex items-center"
                        >
                          <Trash2 size={16} className="mr-1" /> Delete
                        </button>
                      </li>
                    ))}
                  </ul>
                )}
              </div>
            </>
          )}
        </div>
      )}
    </div>
  );
};

export default Dashboard;
