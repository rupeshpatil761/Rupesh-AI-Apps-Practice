# Build stage
FROM node:20-alpine AS builder
WORKDIR /app
ARG VITE_BASE_URL=http://localhost:8080
ENV VITE_BASE_URL=$VITE_BASE_URL
COPY package.json package-lock.json ./
RUN npm ci
COPY . .
RUN npm run build

# Runtime stage
FROM node:20-alpine
WORKDIR /app
RUN npm install -g serve
COPY --from=builder /app/dist ./dist
EXPOSE 3000
CMD ["serve", "-s", "dist", "-l", "3000"]

