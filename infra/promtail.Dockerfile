# Usa una imagen base oficial de Promtail
FROM grafana/promtail:2.9.2

# Copia la configuración
COPY promtail-config.yml /etc/promtail/config.yml

# Comando de inicio
ENTRYPOINT [ "/usr/bin/promtail" ]
CMD [ "-config.file=/etc/promtail/config.yml" ]

# EXPOSE 9080 (opcional, para el Health Check)
EXPOSE 9080