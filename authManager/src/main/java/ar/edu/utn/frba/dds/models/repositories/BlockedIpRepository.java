package ar.edu.utn.frba.dds.models.repositories;

import ar.edu.utn.frba.dds.models.IpStatus;
import ar.edu.utn.frba.dds.models.entities.BlockedIp;
import ar.edu.utn.frba.dds.models.entities.dto.BlockedIpDto;
import java.awt.print.Pageable;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BlockedIpRepository extends JpaRepository<BlockedIp, Long> {
  Boolean existsByIpAndEstado(String ip, IpStatus estado);

  Optional<BlockedIp> findByIp(String ip);

  Boolean existsByIp(String ipToBlock);
}
