import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../../contexts/AuthContext";
import { useToast } from "../../contexts/ToastContext";
import { UserPlus, AlertCircle } from "lucide-react";

const Register = () => {
  const [formData, setFormData] = useState({
    username: "",
    email: "",
    password: "",
    confirmPassword: "",
    role: "student",
  });
  const [errors, setErrors] = useState({});
  const [isSubmitting, setIsSubmitting] = useState(false);

  const { register } = useAuth();
  const { showToast } = useToast();
  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value,
    });

    if (errors[name]) {
      setErrors({
        ...errors,
        [name]: "",
      });
    }
  };

  const validateForm = () => {
    const newErrors = {};

    if (!formData.username) {
      newErrors.username = "Username is required";
    } else if (formData.username.length < 3) {
      newErrors.username = "Username must be at least 3 characters";
    }

    if (!formData.email) {
      newErrors.email = "Email is required";
    } else if (!/\S+@\S+\.\S+/.test(formData.email)) {
      newErrors.email = "Email is invalid";
    }

    const passwordRegex = /^(?=.*[A-Z])(?=.*[!@#$%^&*(),.?":{}|<>]).{8,}$/;

    if (!formData.password) {
      newErrors.password = "Password is required";
    } else if (!passwordRegex.test(formData.password)) {
      newErrors.password =
        "Password must be at least 8 characters, include one uppercase letter and one special symbol";
    }

    if (!formData.confirmPassword) {
      newErrors.confirmPassword = "Please confirm your password";
    } else if (formData.password !== formData.confirmPassword) {
      newErrors.confirmPassword = "Passwords do not match";
    }

    if (!formData.role) {
      newErrors.role = "Please select a role";
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };


  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!validateForm()) {
      return;
    }

    setIsSubmitting(true);

    try {
      const { confirmPassword, ...userData } = formData;

      await register(userData);
      showToast("Registration successful!", "success");
      navigate("/dashboard");
    } catch (error) {
      console.error("Registration error:", error);
      showToast(
        error?.response?.data?.message ||
          "Registration failed. Please try again.",
        "error"
      );
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="min-h-[calc(100vh-12rem)] flex items-center justify-center py-12 animate-fadeIn">
      <div className="max-w-md w-full space-y-8 bg-slate-800 p-8 rounded-lg border border-slate-700">
        <div className="text-center">
          <h2 className="text-3xl font-bold text-white">Create an account</h2>
          <p className="mt-2 text-sm text-slate-400">
            Already have an account?{" "}
            <Link
              to="/login"
              className="text-blue-500 hover:text-blue-400 font-medium"
            >
              Sign in
            </Link>
          </p>
        </div>

        <form className="mt-8 space-y-6" onSubmit={handleSubmit}>
          <div className="space-y-4">
            <div>
              <label htmlFor="username" className="form-label">
                Username
              </label>
              <input
                id="username"
                name="username"
                type="text"
                autoComplete="username"
                value={formData.username}
                onChange={handleChange}
                className={`form-input ${
                  errors.username ? "border-red-500" : ""
                }`}
                placeholder="codemaster"
              />
              {errors.username && (
                <p className="mt-1 text-sm text-red-500 flex items-center">
                  <AlertCircle size={14} className="mr-1" />
                  {errors.username}
                </p>
              )}
            </div>

            <div>
              <label htmlFor="email" className="form-label">
                Email address
              </label>
              <input
                id="email"
                name="email"
                type="email"
                autoComplete="email"
                value={formData.email}
                onChange={handleChange}
                className={`form-input ${errors.email ? "border-red-500" : ""}`}
                placeholder="codemaster@example.com"
              />
              {errors.email && (
                <p className="mt-1 text-sm text-red-500 flex items-center">
                  <AlertCircle size={14} className="mr-1" />
                  {errors.email}
                </p>
              )}
            </div>

            <div>
              <label htmlFor="password" className="form-label">
                Password
              </label>
              <input
                id="password"
                name="password"
                type="password"
                autoComplete="new-password"
                value={formData.password}
                onChange={handleChange}
                className={`form-input ${
                  errors.password ? "border-red-500" : ""
                }`}
                placeholder="••••••••"
              />
              {errors.password && (
                <p className="mt-1 text-sm text-red-500 flex items-center">
                  <AlertCircle size={14} className="mr-1" />
                  {errors.password}
                </p>
              )}
            </div>

            <div>
              <label htmlFor="confirmPassword" className="form-label">
                Confirm Password
              </label>
              <input
                id="confirmPassword"
                name="confirmPassword"
                type="password"
                autoComplete="new-password"
                value={formData.confirmPassword}
                onChange={handleChange}
                className={`form-input ${
                  errors.confirmPassword ? "border-red-500" : ""
                }`}
                placeholder="••••••••"
              />
              {errors.confirmPassword && (
                <p className="mt-1 text-sm text-red-500 flex items-center">
                  <AlertCircle size={14} className="mr-1" />
                  {errors.confirmPassword}
                </p>
              )}
            </div>

            <div>
              <label className="form-label block mb-2">I want to join as</label>
              <div className="flex space-x-3">
                <button
                  type="button"
                  onClick={() => handleChange({ target: { name: "role", value: "student" } })}
                  className={`flex-1 px-4 py-2 rounded-md border text-center ${
                    formData.role === "student"
                      ? "bg-blue-500/20 border-blue-500/50 text-white"
                      : "border-slate-700 text-slate-400 hover:border-blue-500/50"
                  }`}
                >
                  Student
                </button>
                <button
                  type="button"
                  onClick={() => handleChange({ target: { name: "role", value: "teacher" } })}
                  className={`flex-1 px-4 py-2 rounded-md border text-center ${
                    formData.role === "teacher"
                      ? "bg-blue-500/20 border-blue-500/50 text-white"
                      : "border-slate-700 text-slate-400 hover:border-blue-500/50"
                  }`}
                >
                  Teacher
                </button>
              </div>
              {errors.role && (
                <p className="mt-2 text-sm text-red-500 flex items-center">
                  <AlertCircle size={14} className="mr-1" />
                  {errors.role}
                </p>
              )}
            </div>

          </div>

          <div>
            <button
              type="submit"
              className="btn btn-primary w-full"
              disabled={isSubmitting}
            >
              {isSubmitting ? (
                <span className="flex items-center justify-center">
                  <svg
                    className="animate-spin -ml-1 mr-2 h-4 w-4"
                    xmlns="http://www.w3.org/2000/svg"
                    fill="none"
                    viewBox="0 0 24 24"
                  >
                    <circle
                      className="opacity-25"
                      cx="12"
                      cy="12"
                      r="10"
                      stroke="currentColor"
                      strokeWidth="4"
                    ></circle>
                    <path
                      className="opacity-75"
                      fill="currentColor"
                      d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
                    ></path>
                  </svg>
                  Creating account...
                </span>
              ) : (
                <span className="flex items-center justify-center">
                  <UserPlus size={18} className="mr-2" />
                  Create account
                </span>
              )}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default Register;
