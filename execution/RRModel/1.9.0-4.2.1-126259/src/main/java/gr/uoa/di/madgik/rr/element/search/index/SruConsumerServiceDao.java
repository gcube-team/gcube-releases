package gr.uoa.di.madgik.rr.element.search.index;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;

@PersistenceCapable(detachable="true")
@Queries(
		{@Query(
				name="exists", 
				language="JDOQL", 
				value="SELECT this.ID FROM gr.uoa.di.madgik.rr.element.search.index.SruConsumerServiceDao WHERE this.ID == :id"
				),
		@Query(
				name="all", 
				language="JDOQL", 
				value="SELECT this.ID FROM gr.uoa.di.madgik.rr.element.search.index.SruConsumerServiceDao"
				),
		}
		
)
public class SruConsumerServiceDao extends DataSourceServiceDao
{

}
