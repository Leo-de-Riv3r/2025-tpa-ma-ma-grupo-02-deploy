package ar.edu.utn.frba.dds.services;

import ar.edu.utn.frba.dds.models.entities.Lugar;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.index.strtree.STRtree;
import org.opengis.feature.simple.SimpleFeature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Slf4j
@Service
public class GeoToolsProcessorService {
    private STRtree indexProvincias;
    private STRtree indexDepartamentos;

    private Set<String> nombresProvincias = new TreeSet<>();
    private Set<String> nombresDepartamentos = new TreeSet<>();

    private final GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
    private final ResourceLoader resourceLoader;

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
        this.indexProvincias = buildIndex(provinciasPath, "PROVINCIAS", nombresProvincias);
        this.indexDepartamentos = buildIndex(departamentosPath, "DEPARTAMENTOS", nombresDepartamentos);
    }

    private STRtree buildIndex(String path, String capa, Set<String> nombresSet) {
        STRtree index = new STRtree();
        try {
            File file = getFileFromResource(path);

            if (file == null || !file.exists()) {
                log.error("GEOTOOLS: No se pudo encontrar el archivo {}", path);
                return null;
            }

            FileDataStore store = FileDataStoreFinder.getDataStore(file);
            SimpleFeatureSource featureSource = store.getFeatureSource();
            SimpleFeatureCollection collection = featureSource.getFeatures();

            try (SimpleFeatureIterator iterator = collection.features()) {
                while (iterator.hasNext()) {
                    SimpleFeature feature = iterator.next();
                    org.locationtech.jts.geom.Geometry geom = (org.locationtech.jts.geom.Geometry) feature
                            .getDefaultGeometry();

                    if (geom != null) {
                        index.insert(geom.getEnvelopeInternal(), feature);

                        Object nombreObj = feature.getAttribute(COLUMNA_NOMBRE);
                        if (nombreObj != null)
                            nombresSet.add(nombreObj.toString().trim());
                    }
                }
            }
            index.build();
            log.info("GEOTOOLS: Índice espacial construido para {} con {} items.", capa, index.size());
            return index;
        } catch (Exception e) {
            log.error("GEOTOOLS: Error cargando {}", capa, e);
            e.printStackTrace();
            return null;
        }
    }

    private File getFileFromResource(String resourcePath) {
        try {
            Resource resource = resourceLoader.getResource(resourcePath);

            if (resource.isFile()) {
                return resource.getFile();
            }

            log.info("GEOTOOLS: Extrayendo recurso desde JAR a Temp: {}", resourcePath);

            Path tempDir = Files.createTempDirectory("geotools_data");
            String filename = resource.getFilename();
            if (filename == null)
                return null;
            String baseName = filename.replace(".shp", "");

            String[] extensions = { ".shp", ".shx", ".dbf", ".prj", ".cpg", ".fix" };

            File mainFile = null;

            String cleanPath = resourcePath.replace("classpath:", "");
            String folderPath = cleanPath.substring(0, cleanPath.lastIndexOf('/') + 1); // ej: "geodata/"

            for (String ext : extensions) {
                try {
                    String siblingPath = "classpath:" + folderPath + baseName + ext;
                    Resource siblingResource = resourceLoader.getResource(siblingPath);

                    if (siblingResource.exists()) {
                        File tempFile = new File(tempDir.toFile(), baseName + ext);
                        try (InputStream is = siblingResource.getInputStream();
                                FileOutputStream os = new FileOutputStream(tempFile)) {
                            FileCopyUtils.copy(is, os);
                        }
                        if (ext.equals(".shp")) {
                            mainFile = tempFile;
                        }
                    }
                } catch (Exception ex) {
                    log.debug("No se encontró extensión opcional {} para {}", ext, baseName);
                }
            }

            return mainFile;

        } catch (Exception e) {
            log.error("Error extrayendo archivo de recursos", e);
            return null;
        }
    }

    public Lugar buscarPorPoligono(double latitud, double longitud) {
        if (indexProvincias == null && indexDepartamentos == null)
            return null;

        Point punto = geometryFactory.createPoint(new Coordinate(longitud, latitud));
        Lugar lugar = new Lugar();

        if (indexDepartamentos != null) {
            SimpleFeature depto = buscarEnIndice(indexDepartamentos, punto);
            if (depto != null) {
                Object val = depto.getAttribute(COLUMNA_NOMBRE);
                if (val != null) {
                    lugar.setDepartamento(val.toString());
                    lugar.setMunicipio(val.toString());
                }
            }
        }

        if (indexProvincias != null) {
            SimpleFeature prov = buscarEnIndice(indexProvincias, punto);
            if (prov != null) {
                Object val = prov.getAttribute(COLUMNA_NOMBRE);
                if (val != null)
                    lugar.setProvincia(val.toString());
            }
        }

        if (lugar.getProvincia() != null)
            return lugar;
        return null;
    }

    private SimpleFeature buscarEnIndice(STRtree index, Point punto) {
        List<?> candidatos = index.query(punto.getEnvelopeInternal());

        for (Object obj : candidatos) {
            SimpleFeature feature = (SimpleFeature) obj;
            org.locationtech.jts.geom.Geometry geom = (org.locationtech.jts.geom.Geometry) feature.getDefaultGeometry();
            if (geom.contains(punto)) {
                return feature;
            }
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