package ar.edu.utn.frba.dds.services;

import ar.edu.utn.frba.dds.exceptions.IpAlreadyBlockedException;
import ar.edu.utn.frba.dds.models.IpStatus;
import ar.edu.utn.frba.dds.models.entities.BlockedIp;
import ar.edu.utn.frba.dds.models.entities.BlockedIpConverter;
import ar.edu.utn.frba.dds.models.entities.dto.BlockedIpDto;
import ar.edu.utn.frba.dds.models.entities.dto.PaginacionDto;
import ar.edu.utn.frba.dds.models.repositories.BlockedIpRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class IpManagerService implements IpManagerServiceInterface{
  private final BlockedIpRepository blockedIpRepository;

  public IpManagerService(BlockedIpRepository blockedIpRepository) {
    this.blockedIpRepository = blockedIpRepository;
  }

  public Boolean isIpBlocked(String ipToSearch) {
    return blockedIpRepository.existsByIpAndEstado(ipToSearch, IpStatus.BLOCKED);
  }

  public void blockNewIp(String ipToBlock, String motivo) {
    Boolean exists = blockedIpRepository.existsByIp(ipToBlock);
    if (!exists) {
      BlockedIp blockedIp = new BlockedIp();
      blockedIp.setIp(ipToBlock);
      blockedIp.setMotivo(motivo);
      blockedIpRepository.save(blockedIp);
    } else {
      if (blockedIpRepository.existsByIpAndEstado(ipToBlock, IpStatus.BLOCKED)) {
        throw new IpAlreadyBlockedException("La IP ya se encuentra bloqueada");
      } else {
        BlockedIp blockedIp = blockedIpRepository.findByIp(ipToBlock).orElseThrow(
            () -> new EntityNotFoundException("Ip no encontrada")
        );

        blockedIp.setEstado(IpStatus.BLOCKED);
        blockedIp.setFechaModificacion(LocalDateTime.now());
        blockedIp.setMotivo(motivo);
        blockedIpRepository.save(blockedIp);
      }
    }
  }

  public void unblock(String ipToUnblock, String motivo) {
    Boolean exists = blockedIpRepository.existsByIp(ipToUnblock);
    Boolean blocked = blockedIpRepository.existsByIpAndEstado(ipToUnblock, IpStatus.BLOCKED);
    if (!exists || !blocked) {
      throw new IpAlreadyBlockedException("La IP no se encuentra bloqueada");
    }

    BlockedIp blockedIp = blockedIpRepository.findByIp(ipToUnblock).orElseThrow(
        () -> new EntityNotFoundException("Ip no encontrada")
    );

    blockedIp.setEstado(IpStatus.UNLOCKED);
    blockedIp.setFechaModificacion(LocalDateTime.now());
    blockedIp.setMotivo(motivo);
    blockedIpRepository.save(blockedIp);
  }

  @Override
  public PaginacionDto<BlockedIpDto> getIpList(int page, int perPage) {
    if (page < 1) page = 1;
    if (perPage < 1 || perPage > 30) perPage = 30;

    Pageable pageable = PageRequest.of(page - 1, perPage, Sort.by("fechaModificacion").descending());

    Page<BlockedIp> pageResult;

    pageResult = blockedIpRepository.findAll(pageable);

    List<BlockedIpDto> ipsDto = pageResult.getContent()
        .stream()
        .map(BlockedIpConverter::fromEntity)
        .toList();

    return new PaginacionDto<>(
        ipsDto,
        pageable.getPageNumber() + 1,
        pageResult.getTotalPages()
    );
  }
}
