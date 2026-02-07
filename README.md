<div align="center">
  <img alt="ClashOfCode-Logo" src="README_icon.png" weight=200 height=200 />
  <h1 padding=>Clash Of Code</h1>

</div>

<h2>Built With</h2>

* <img height="14" width="14" src="https://cdn.simpleicons.org/react/white" /> React
* <img height="14" width="14" src="https://cdn.simpleicons.org/vite/white" /> Vite
* <img height="14" width="14" src="https://cdn.simpleicons.org/tailwindcss/white" /> Tailwind CSS
* <img height="14" width="14" src="https://cdn.simpleicons.org/spring/white" /> Spring
* <img height="14" width="14" src="https://cdn.simpleicons.org/postgresql/white" /> PostgreSQL
* <img height="14" width="14" src="https://cdn.simpleicons.org/gmail/white" /> Gmail (Password Reset)
* OneCompiler (Code Execution API)

<h2>Getting Started</h2>

1. Clone the repo

   ```bash
   git clone https://github.com/KhaidarovNurlan/Clash-Of-Code.git
   ```
2. Install all dependencies

   ```bash
   npm i
   ```

3. Create `server/src/main/resources/application-dev.properties`

   ```env
   DB_USERNAME=...
   DB_PASSWORD=...
   JWT_SECRET=...
   ONECOMPILER_KEY=...
   MAILTRAP_USERNAME=...
   MAILTRAP_PASSWORD=...

   spring.jpa.show-sql=true
   spring.sql.init.mode=always
   spring.sql.init.platform=postgres

   logging.level.org.springframework.web=DEBUG
   logging.level.com.server=DEBUG
   ```

3. Create `.env`

   ```env
   VITE_API_URL=...
   ```

4. Start an application

   ```bash
   npm run dev
   ```

   ```bash
   cd server
   mvn spring-boot:run "-Dspring-boot.run.profiles=dev"
   ```