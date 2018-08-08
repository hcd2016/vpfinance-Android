package cn.vpfinance.vpjr.network;

/**
 * Description：接口API，必须为Observable<HttpResult<T>>类型
 * T 根据data中返回的数据定义实体，如Observable<HttpResult<User>>
 * 如果data为空或字符串等，直接使用Observable<HttpResult<String>>
 */
public interface ApiService {

}
