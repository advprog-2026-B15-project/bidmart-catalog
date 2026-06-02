# Gunakan JRE yang ringan untuk menjalankan aplikasi
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Ambil file JAR yang sudah dibangun oleh GitHub Actions
# File JAR harus diletakkan di folder yang sama dengan Dockerfile sebelum build
COPY build/libs/*.jar app.jar

# Buat folder uploads jika belum ada
RUN mkdir -p uploads

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]
