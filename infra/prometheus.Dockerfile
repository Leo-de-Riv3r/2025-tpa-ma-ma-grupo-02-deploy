FROM prom/prometheus:v2.51.0
COPY prometheus.yml /etc/prometheus/prometheus.yml
# Railway usa puertos dinámicos a veces, pero Prometheus expone 9090 por defecto
CMD [ "--config.file=/etc/prometheus/prometheus.yml" ]