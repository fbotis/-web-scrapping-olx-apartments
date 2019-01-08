package lecturaudio.dao;

import lecturaudio.model.Audiobook;
import org.springframework.data.repository.CrudRepository;

public interface AudioBookRepository extends CrudRepository<Audiobook, Long> {

}
