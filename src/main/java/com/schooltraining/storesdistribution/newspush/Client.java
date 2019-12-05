package com.schooltraining.storesdistribution.newspush;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.yeauty.pojo.Session;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Client implements Serializable {

    private static final long serialVersionUID = 8957107006902627635L;

    private String userId;
    private String shopId;

    private String userName;

    private Session session;//通过session来保持连接

}
