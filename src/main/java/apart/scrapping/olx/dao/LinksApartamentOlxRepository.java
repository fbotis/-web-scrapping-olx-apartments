package apart.scrapping.olx.dao;

import apart.scrapping.olx.dao.model.LinkApartamentOlx;
import org.springframework.data.repository.CrudRepository;

public interface LinksApartamentOlxRepository extends CrudRepository<LinkApartamentOlx, Long> {

	LinkApartamentOlx[] findByProcessed(boolean b);
}
