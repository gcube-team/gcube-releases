/*
package gr.cite.regional.data.collection.application.cache;

import com.google.common.cache.CacheLoader;
import gr.cite.regional.data.collection.dataaccess.entities.UserReference;
import gr.cite.regional.data.collection.dataaccess.services.UserReferenceService;
import org.springframework.beans.factory.annotation.Autowired;


public class UserInfoCache extends CacheLoader<String, UserReference> {
	private UserReferenceService userReferenceService;
	
	@Autowired
	public UserInfoCache(UserReferenceService userReferenceService) {
		this.userReferenceService = userReferenceService;
	}
	
	*/
/*public static LoadingCache<String, UserInfo> cache = CacheBuilder.newBuilder()
			.expireAfterAccess(3, TimeUnit.SECONDS)
			.maximumSize(10)
			.weakKeys()
			.build(new CacheLoader<String, UserInfo>() {
				
				@Override
				public UserInfo load(String accessToken) throws Exception {
					return new UserInfo();
				}
				
			});*//*

	
	@Override
	public UserReference load(String accessToken) throws Exception {
		return this.userReferenceService.getUserReference(0);
	}
}
*/
