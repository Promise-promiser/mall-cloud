package com.sql.cloud.mall.practice.cartorder.service;

import com.sql.cloud.mall.practice.cartorder.model.vo.CartVO;
import java.util.List;

public interface CartService {
    List<CartVO> list(Integer userId);

    List<CartVO> add(Integer uerId, Integer productId, Integer count);

    List<CartVO> update(Integer uerId, Integer productId, Integer count);

    List<CartVO> delete(Integer uerId, Integer productId);

    List<CartVO> selectOrNot(Integer userId, Integer productId, Integer selected);

    List<CartVO> selectAllOrNot(Integer userId, Integer selected);
}
