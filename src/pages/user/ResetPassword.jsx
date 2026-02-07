import { useState, useEffect } from "react";
import { useSearchParams, useNavigate, Link } from "react-router-dom";
import axios from "axios";
import { useToast } from "../../contexts/ToastContext";
import { KeyRound, ArrowLeft } from "lucide-react";

const ResetPassword = () => {
  const [searchParams] = useSearchParams();
  const [newPassword, setNewPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const { showToast } = useToast();
  const navigate = useNavigate();

  const token = searchParams.get("token");

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!token) {
      showToast("Токен отсутствует. Пожалуйста, воспользуйтесь ссылкой из письма.", "error");
      return;
    }

    setLoading(true);
    try {
      const res = await axios.post("http://localhost:8080/reset/confirm", {
        token,
        newPassword,
      });

      if (res.data.status === "error") {
        showToast(res.data.message, "error");
      } else {
        showToast("Пароль успешно изменен!", "success");
        navigate("/login");
      }
    } catch (err) {
      showToast(err.response?.data?.message || "Ошибка при сбросе пароля", "error");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-[calc(100vh-12rem)] flex items-center justify-center py-12 animate-fadeIn">
      <div className="max-w-md w-full bg-slate-800 p-8 rounded-lg border border-slate-700 space-y-6">
        <div className="text-center">
          <h2 className="text-3xl font-bold text-white">New Password</h2>
          <p className="mt-2 text-slate-400 text-sm">
            Create a strong password for your account.
          </p>
        </div>

        {!token ? (
          <div className="bg-red-500/10 border border-red-500/50 p-4 rounded text-red-400 text-sm">
            The link is invalid or the token is missing. Please request a password reset again.
          </div>
        ) : (
          <form onSubmit={handleSubmit} className="space-y-4">
            <input
              type="password"
              className="form-input"
              placeholder="..."
              value={newPassword}
              onChange={(e) => setNewPassword(e.target.value)}
              minLength={8}
              required
            />

            <button
              type="submit"
              className="btn btn-primary w-full"
              disabled={loading}
            >
              {loading ? "Updating..." : <><KeyRound size={16} className="mr-2" /> Reset Password</>}
            </button>
          </form>
        )}

        <div className="text-center mt-4">
          <Link to="/login" className="text-slate-400 text-sm hover:text-blue-400">
            <ArrowLeft size={14} className="inline mr-1" /> Back to Login
          </Link>
        </div>
      </div>
    </div>
  );
};

export default ResetPassword;