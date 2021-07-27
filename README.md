docker build -f Dockerfile -t reviews-app .
docker run -p 8080:8080 reviews-app 
