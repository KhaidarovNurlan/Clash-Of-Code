CREATE TABLE IF NOT EXISTS users (
  id SERIAL PRIMARY KEY,
  username VARCHAR(50) NOT NULL UNIQUE,
  email VARCHAR(100) NOT NULL UNIQUE,
  password VARCHAR(100) NOT NULL,
  role VARCHAR(20) NOT NULL DEFAULT 'student',
  points INTEGER DEFAULT 0,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS password_reset_token (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    token VARCHAR(255) NOT NULL,
    expires_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS programming_languages (
  id SERIAL PRIMARY KEY,
  name VARCHAR(50) NOT NULL,
  code VARCHAR(20) NOT NULL UNIQUE
);

INSERT INTO programming_languages (name, code)
VALUES 
  ('Python', 'python'),
  ('Java', 'java'),
  ('C#', 'csharp'),
  ('Javascript', 'javascript')
ON CONFLICT (code) DO NOTHING;

CREATE TABLE IF NOT EXISTS courses (
  id SERIAL PRIMARY KEY,
  title VARCHAR(100) NOT NULL,
  description TEXT,
  level VARCHAR(20) NOT NULL,
  created_by INTEGER REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS lessons (
  id SERIAL PRIMARY KEY,
  course_id INTEGER REFERENCES courses(id) ON DELETE CASCADE,
  title VARCHAR(100) NOT NULL,
  content TEXT NOT NULL,
  order_number INTEGER NOT NULL,
  points INTEGER DEFAULT 0
);

CREATE TABLE IF NOT EXISTS user_courses (
  id SERIAL PRIMARY KEY,
  user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
  course_id INTEGER REFERENCES courses(id) ON DELETE CASCADE,
  progress INTEGER DEFAULT 0,
  completed BOOLEAN DEFAULT FALSE,
  last_accessed TIMESTAMP,
  UNIQUE(user_id, course_id)
);

CREATE TABLE IF NOT EXISTS user_lessons (
  id SERIAL PRIMARY KEY,
  user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
  lesson_id INTEGER REFERENCES lessons(id) ON DELETE CASCADE,
  completed BOOLEAN DEFAULT FALSE,
  completed_at TIMESTAMP,
  UNIQUE(user_id, lesson_id)
);

CREATE TABLE IF NOT EXISTS tournaments (
  id SERIAL PRIMARY KEY,
  title VARCHAR(100) NOT NULL,
  description TEXT,
  difficulty VARCHAR(20),
  created_by INTEGER REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS tournament_languages (
  id SERIAL PRIMARY KEY,
  tournament_id INTEGER REFERENCES tournaments(id) ON DELETE CASCADE,
  language_code VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS tournament_levels (
  id SERIAL PRIMARY KEY,
  tournament_id INTEGER REFERENCES tournaments(id) ON DELETE CASCADE,
  level_number INTEGER NOT NULL,
  expected_output TEXT,
  required_keywords TEXT[],
  points INTEGER DEFAULT 0
);

CREATE TABLE IF NOT EXISTS tournament_submissions (
  id SERIAL PRIMARY KEY,
  user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
  tournament_id INTEGER REFERENCES tournaments(id) ON DELETE CASCADE,
  level_id INTEGER REFERENCES tournament_levels(id) ON DELETE CASCADE,
  passed BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS tournament_completions (
  id SERIAL PRIMARY KEY,
  user_id INT NOT NULL,
  tournament_id INT NOT NULL,
  completion_time INTEGER NOT NULL,
  completed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (tournament_id) REFERENCES tournaments(id) ON DELETE CASCADE,
  UNIQUE (user_id, tournament_id)
);
