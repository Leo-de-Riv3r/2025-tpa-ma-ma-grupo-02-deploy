FROM grafana/loki:2.9.0
COPY loki-config.yml /etc/loki-config.yml
CMD [ "-config.file=/etc/loki-config.yml" ]