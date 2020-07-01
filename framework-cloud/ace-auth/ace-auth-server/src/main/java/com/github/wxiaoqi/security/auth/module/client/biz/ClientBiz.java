package com.github.wxiaoqi.security.auth.module.client.biz;

import com.github.wxiaoqi.security.auth.module.client.entity.Client;
import com.github.wxiaoqi.security.auth.module.client.entity.ClientService;
import com.github.wxiaoqi.security.auth.module.client.mapper.ClientMapper;
import com.github.wxiaoqi.security.auth.module.client.mapper.ClientServiceMapper;
import com.github.wxiaoqi.security.common.biz.BaseBiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author Mr.AG
 * @version 2017-12-26 19:43:46
 * @email 463540703@qq.com
 */
@Service
public class ClientBiz extends BaseBiz<ClientMapper, Client> {
    @Autowired
    private ClientServiceMapper clientServiceMapper;
    @Autowired
    private ClientServiceBiz clientServiceBiz;

    public List<Client> getClientServices(String id) {
        return mapper.selectAuthorityServiceInfo(id);
    }

    public void modifyClientServices(String id, String clients) {
        clientServiceMapper.deleteByServiceId(id);
        if (!StringUtils.isEmpty(clients)) {
            String[] mem = clients.split(",");
            for (String m : mem) {
                ClientService clientService = new ClientService();
                clientService.setServiceId(m);
                clientService.setClientId(id + "");
                clientServiceBiz.insertSelective(clientService);
            }
        }
    }
}
