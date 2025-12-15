package ar.edu.utn.frba.dds.services;

import ar.edu.utn.frba.dds.models.entities.Lugar;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.opengis.feature.simple.SimpleFeature;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import lombok.extern.slf4j.Slf4j;
import java.util.Set;
import java.util.TreeSet;
import java.util.List;
import java.util.ArrayList;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.net.URL;

@Slf4j
@Service
public class GeoToolsProcessorService {

  private SimpleFeatureCollection provinciasCollection;
  private SimpleFeatureCollection departamentosCollection;
  private final GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
  private final ResourceLoader resourceLoader;
  private Set<String> nombresProvincias = new TreeSet<>();
  private Set<String> nombresDepartamentos = new TreeSet<>();

  @Value("${geotools.shapefile.provincias.path}")
  private String provinciasPath;

  @Value("${geotools.shapefile.departamentos.path}")
  private String departamentosPath;

  private static final String COLUMNA_NOMBRE = "NAME";

  public GeoToolsProcessorService(ResourceLoader resourceLoader) {
    this.resourceLoader = resourceLoader;
  }

  @PostConstruct
  public void init() {
    this.provinciasCollection = loadShapefile(provinciasPath, "PROVINCIAS");
    if (this.provinciasCollection != null) {
      extraerNombresUnicos(this.provinciasCollection, nombresProvincias);
    }

    // Carga de Departamentos
    this.departamentosCollection = loadShapefile(departamentosPath, "DEPARTAMENTOS");
    if (this.departamentosCollection != null) {
      extraerNombresUnicos(this.departamentosCollection, nombresDepartamentos);
    }
  }

  private void extraerNombresUnicos(SimpleFeatureCollection collection, Set<String> targetSet) {
    try (SimpleFeatureIterator iterator = collection.features()) {
      while (iterator.hasNext()) {
        SimpleFeature feature = iterator.next();
        Object nombreObj = feature.getAttribute(COLUMNA_NOMBRE);
        if (nombreObj != null) {
          targetSet.add(nombreObj.toString().trim());
        }
      }
      log.info("GEOTOOLS: Extraídos {} nombres únicos para autocompletado.", targetSet.size());
    } catch (Exception e) {
      log.error("GEOTOOLS ERROR: Falló la extracción de nombres.", e);
    }
  }

  private SimpleFeatureCollection loadShapefile(String path, String capa) {
    try {
      log.info("GEOTOOLS: Cargando {} desde: {}", capa, path);
      URL url = resourceLoader.getResource(path).getURL();
      File file = new File(url.toURI());

      if (file.exists()) {
        FileDataStore store = FileDataStoreFinder.getDataStore(file);
        SimpleFeatureSource featureSource = store.getFeatureSource();
        SimpleFeatureCollection collection = featureSource.getFeatures();
        log.info("GEOTOOLS: {} cargada correctamente ({} elementos).", capa, collection.size());
        return collection;
      } else {
        log.error("GEOTOOLS: Archivo no encontrado para {}: {}", capa, path);
      }
    } catch (Exception e) {
      log.error("GEOTOOLS ERROR: Falló la carga de {}.", capa, e);
    }
    return null;
  }

  public Lugar buscarPorPoligono(double latitud, double longitud) {
    if (provinciasCollection == null && departamentosCollection == null)
      return null;

    Point punto = geometryFactory.createPoint(new Coordinate(longitud, latitud));
    Lugar lugar = new Lugar();

    if (departamentosCollection != null) {
      SimpleFeature deptoFeature = buscarFeatureEnColeccion(departamentosCollection, punto);
      if (deptoFeature != null) {
        Object nombreDepto = deptoFeature.getAttribute(COLUMNA_NOMBRE);
        if (nombreDepto != null) {
          lugar.setDepartamento(nombreDepto.toString());
          lugar.setMunicipio(nombreDepto.toString());
        }
      }
    }

    if (provinciasCollection != null) {
      SimpleFeature provFeature = buscarFeatureEnColeccion(provinciasCollection, punto);
      if (provFeature != null) {
        Object nombreProv = provFeature.getAttribute(COLUMNA_NOMBRE);
        if (nombreProv != null) {
          lugar.setProvincia(nombreProv.toString());
        }
      }
    }

    if (lugar.getProvincia() != null && !lugar.getProvincia().isEmpty()) {
      if (lugar.getDepartamento() == null)
        lugar.setDepartamento("Sin datos");
      if (lugar.getMunicipio() == null)
        lugar.setMunicipio("Sin datos");
      return lugar;
    }

    return null;
  }

  private SimpleFeature buscarFeatureEnColeccion(SimpleFeatureCollection collection, Point punto) {
    try (SimpleFeatureIterator iterator = collection.features()) {
      while (iterator.hasNext()) {
        SimpleFeature feature = iterator.next();
        org.locationtech.jts.geom.Geometry geometry = (org.locationtech.jts.geom.Geometry) feature
            .getDefaultGeometry();

        if (geometry != null && geometry.contains(punto)) {
          return feature;
        }
      }
    } catch (Exception e) {
      log.error("GEOTOOLS ERROR: Falló la búsqueda espacial.", e);
    }
    return null;
  }

  public List<String> getNombresProvincias() {
    return new ArrayList<>(nombresProvincias);
  }

  public List<String> getNombresDepartamentos() {
    return new ArrayList<>(nombresDepartamentos);
  }
}