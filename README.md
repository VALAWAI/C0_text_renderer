# C0 Text Renderer

The **C0 Text Renderer** is a web-based interface designed to render and display text received via the VALAWAI ecosystem. It supports multiple formats, including plain text, Markdown, and HTML, providing a flexible way to visualize data streams in real-time.

## Summary

- **Component Type**: [C0](https://valawai.github.io/docs/components/C0/)
- **Component Name**: Text Renderer
- **Documentation**: [Full Documentation](https://valawai.github.io/docs/components/C0/text_renderer)
- **Versions**:
  - **Stable Version**: [1.0.0 (November 17, 2026)](https://github.com/VALAWAI/C0_text_renderer/tree/1.0.0)
  - **API**: [1.0.0 (November 17, 2026)](https://raw.githubusercontent.com/VALAWAI/C0_text_renderer/ASYNCAPI_1.0.0/asyncapi.yml)
  - **Required MOV API**: [1.2.0 (March 9, 2024)](https://raw.githubusercontent.com/valawai/MOV/ASYNCAPI_1.2.0/asyncapi.yml)
- **Developed By**: [IIIA-CSIC](https://www.iiia.csic.es)
- **License**: [GPL v3](LICENSE)
- **Technology Readiness Level (TRL)**: [3](https://valawai.github.io/docs/components/C0/text_renderer/tlr)

## Usage

The Text Renderer is used to visualize received text content. It monitors the `valawai/c0/text_renderer/data/text` channel for incoming messages and renders them within a web application. 

**Supported Formats:**
- Plain Text
- Markdown
- HTML

## Deployment

The **C0 Text Renderer** is designed to run as a Docker container within the [Master Of VALAWAI (MOV)](https://valawai.github.io/docs/architecture/implementations/mov) ecosystem. For a comprehensive guide and advanced configuration, refer to the [deployment documentation](https://valawai.github.io/docs/components/C0/text_renderer/deploy).

### Quick Start Guide

#### 1. Build the Docker Image
From the project root directory, build the container image:
```bash
./buildDockerImages.sh -t latest
```
This creates the `valawai/c0_text_renderer:latest` image used by the deployment configuration.

#### 2. Start the Component
You can launch the component in two different modes:

**A. Integrated with MOV**
To run the Text Renderer along with the full MOV stack:
```bash
COMPOSE_PROFILES=all docker compose up -d
```
**Access points:**
- **MOV:** [http://localhost:8081](http://localhost:8081)
- **RabbitMQ UI:** [http://localhost:8082](http://localhost:8082) (User: `mov`, Pass: `password`)
- **MongoDB:** `localhost:27017` (User: `mov`, Pass: `password`)

**B. Standalone Mode**
To connect to an existing MOV or remote RabbitMQ instance, create a `.env` file in the directory containing `docker-compose.yml`:
```properties
MOV_MQ_HOST=host.docker.internal
MOV_MQ_USERNAME=mov
MOV_MQ_PASSWORD=password
C0_TEXT_RENDERER_PORT=9080
```
Then start only the component:
```bash
COMPOSE_PROFILES=component docker compose up -d
```

#### 3. Stop the Containers
To shut down all active services:
```bash
COMPOSE_PROFILES=all docker compose down
```

## Development Environment

To ensure a consistent and isolated development experience, this project utilizes Docker to provide a self-contained environment with all necessary tools, minimizing "it works on my machine" issues.

### Launching the Environment
Start the development environment using the provided script:
```bash
./startDevelopmentEnvironment.sh
```

Once launched, you will enter a bash shell with access to the following integrated tools:
- **Master of VALAWAI**: Central topology manager ([http://localhost:8081](http://localhost:8081)).
- **RabbitMQ**: Message broker ([http://localhost:8082](http://localhost:8082), `mov:password`).
- **MongoDB**: Project database (`movDB`, `mov:password`).
- **Mongo Express**: Web GUI for MongoDB ([http://localhost:8084](http://localhost:8084), `mov:password`).

### Common Development Commands
Within the development console, you can use the official `quarkus` CLI or these shortcuts:
- `startServer`: Start the development server.
- `mvn clean`: Clean compiled and generated code.
- `mvn test`: Run all project tests.
- `mvn -DuseDevMOV=true test`: Run tests using the active MOV instance.

To shut down the environment, type `exit` or run:
```bash
./stopDevelopmentEnvironment.sh
```

## Helpful Links

- **C0 Text Renderer Documentation**: [Link](https://valawai.github.io/docs/components/C0/text_renderer)
- **Master Of VALAWAI (MOV)**: [Architecture & Implementation](https://valawai.github.io/docs/architecture/implementations/mov/)
- **VALAWAI Main Documentation**: [Explore the Ecosystem](https://valawai.github.io/docs/)
- **GitHub Organization**: [VALAWAI on GitHub](https://github.com/VALAWAI)
- **Official Website**: [valawai.eu](https://valawai.eu/)
- **Twitter/X**: [@ValawaiEU](https://x.com/ValawaiEU)
