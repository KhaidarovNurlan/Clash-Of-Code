import { useState } from "react";
import axios from "axios";
import { useToast } from "../../contexts/ToastContext";
import { ArrowLeft, Send, Mail } from "lucide-react";
import { Link } from "react-router-dom";

const ForgotPassword = () => {
  const [email, setEmail] = useState("");
  const [isSent, setIsSent] = useState(false);
  const [loading, setLoading] = useState(false);
  const { showToast } = useToast();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      await axios.post("http://localhost:8080/reset/request", { email });

      setIsSent(true);
      showToast("Reset instructions have been sent to your email!", "success");
    } catch (err) {
      console.error(err);
      showToast(err.response?.data?.message || "Error requesting reset", "error");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-[calc(100vh-12rem)] flex items-center justify-center py-12 animate-fadeIn">
      <div className="max-w-md w-full bg-slate-800 p-8 rounded-lg border border-slate-700 space-y-6">

        {!isSent ? (
          <>
            <div className="text-center">
              <h2 className="text-3xl font-bold text-white">Forgot password?</h2>
              <p className="mt-2 text-slate-400 text-sm">
                Enter your email and we’ll generate a recovery link.
              </p>
            </div>

            <form onSubmit={handleSubmit} className="space-y-4">
              <input
                type="email"
                className="form-input"
                placeholder="..."
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
              />

              <button
                type="submit"
                className="btn btn-primary w-full"
                disabled={loading}
              >
                {loading ? "Sending..." : <><Send size={16} className="mr-2" /> Send Link</>}
              </button>
            </form>
          </>
        ) : (
          <div className="text-center space-y-4 py-4">
            <div className="flex justify-center">
              <div className="bg-blue-500/20 p-4 rounded-full">
                <Mail size={40} className="text-blue-400" />
              </div>
            </div>
            <h2 className="text-2xl font-bold text-white">Check your email</h2>
            <p className="text-slate-400 text-sm">
              We've sent an email to <b>{email}</b> with a link to reset your password.
              The link will be active for 24 hours.
            </p>
            <button
              onClick={() => setIsSent(false)}
              className="text-sm text-blue-400 hover:underline"
            >
              Did not receive the link? Try again
            </button>
          </div>
        )}

        <div className="text-center mt-4 border-t border-slate-700 pt-4">
          <Link to="/login" className="text-slate-400 text-sm hover:text-blue-400">
            <ArrowLeft size={14} className="inline mr-1" /> Back to Login
          </Link>
        </div>
      </div>
    </div>
  );
};

export default ForgotPassword;