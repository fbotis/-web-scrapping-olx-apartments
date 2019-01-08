package parser.olx.dao;

import org.springframework.data.repository.CrudRepository;
import parser.olx.dao.model.OlxItemLink;

public interface OlxItemLinkRepository extends CrudRepository<OlxItemLink, Long> {

}
