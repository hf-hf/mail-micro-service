docker run \
	--name mail-micro-service \
	-e JVMXMX=500m \
	-v /app/app-log:/app/app-log \
	-v /app/h2:/app/h2 \
	-p 12345:12345 \
	-d mail-micro-service