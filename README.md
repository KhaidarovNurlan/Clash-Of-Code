<div align="center">
  <img alt="ClashOfCode-Logo" src="README_icon.png" weight=200 height=200 />
  <h1 padding=>Clash Of Code</h1>

  [![Figma](https://img.shields.io/badge/Click_for_Figma-333333?logo=figma
  )](https://www.figma.com/design/96HxzCo3f1FkZiD7vkp1FT/Clash-Of-Code-%7C-Learning-programming-languages-%E2%80%8B%E2%80%8Bwith-gamification-elements?node-id=0-1&t=CUKZY5Qi2xiOUnzW-1) ![Figma](https://img.shields.io/badge/MIT-License-333333)

</div>

<h2>Built With</h2>

* <img height="14" width="14" src="https://cdn.simpleicons.org/react/white" /> React
* <img height="14" width="14" src="https://cdn.simpleicons.org/vite/white" /> Vite
* <img height="14" width="14" src="https://cdn.simpleicons.org/tailwindcss/white" /> Tailwind CSS
* <img height="14" width="14" src="https://cdn.simpleicons.org/spring/white" /> Spring
* <img height="14" width="14" src="https://cdn.simpleicons.org/postgresql/white" /> PostgreSQL
* <img height="14" width="14" src="https://cdn.simpleicons.org/gmail/white" /> Gmail (Password Reset)
* OneCompiler (Code Execution API)
* <img height="14" width="14" src="https://cdn.simpleicons.org/docker/white" /> Docker
* <img height="14" width="14" src="https://cdn.simpleicons.org/apachemaven/white" /> Apache Maven
* <img height="14" width="14" src="https://cdn.simpleicons.org/nginx/white" /> Nginx

<h2>Getting Started</h2>

1. Clone the repo

   ```bash
   git clone https://github.com/KhaidarovNurlan/Clash-Of-Code.git
   ```
2. Install all dependencies

   ```bash
   npm i
   ```

3. Create `server/.env`

   ```env
   DB_USERNAME=...
   DB_PASSWORD=...
   JWT_SECRET=...
   ONECOMPILER_KEY=...
   GMAIL_USERNAME=...
   GMAIL_APP_PASSWORD=...
   ```

4. Start an application

   ```bash
   npm run dev
   ```

   ```bash
   cd server
   mvn spring-boot:run
   ```

<h2>Docker</h2>

1. Change host to `db` in `server/src/main/resources/application.properties`

   ```env
   spring.datasource.url=jdbc:postgresql://db:5432/clashofcode
   ```

2. Create `.env`

   ```env
   DB_USERNAME=...
   DB_PASSWORD=...
   JWT_SECRET=...
   ONECOMPILER_KEY=...
   GMAIL_USERNAME=...
   GMAIL_APP_PASSWORD=...
   ```
3. Run docker-compose

   ```bash
   docker-compose up --build
   ```