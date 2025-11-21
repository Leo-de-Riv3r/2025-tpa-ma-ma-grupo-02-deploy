FROM grafana/grafana:10.4.1
# Copiamos la configuración de datasources para que Grafana sepa dónde están Tempo y Prometheus
COPY grafana-datasources.yml /etc/grafana/provisioning/datasources/datasources.yml