package com.ilyak.repository;


import io.micronaut.core.util.CollectionUtils;
import io.micronaut.data.annotation.Repository;
import io.micronaut.transaction.annotation.ReadOnly;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class TransactionalRepository {

    @PersistenceContext
    EntityManager manager;


    @Transactional
    public Optional<String> genOid(){
        return Optional.of(String.valueOf(manager.createNativeQuery("select gen_id()").getSingleResult()));
    }

    @Transactional
    @ReadOnly
    public Map<String, Object> getReport(String oid){
        Object[] result = (Object[]) manager.createNativeQuery("select t.user_name," +
                        "t.user_phone_number," +
                        "t.user_rating," +
                        "extract(day from now()- t.user_reg_date)," +
                        "count(ro.oid) as user_send_offer," +
                        "count(rop.oid) as user_receive_offer," +
                        "count(ct_as_renter.oid) as user_contracts_as_renter," +
                        "count(ct_as_owner.oid) as user_contracts_as_owner " +
                        "from users as t left join rent_offer ro on t.oid = ro.rent_offer_renter " +
                        "                left join post p on t.oid = p.post_creator_oid " +
                        "                left join rent_offer rop on p.oid = rop.rent_offer_post " +
                        "                left join contract ct_as_renter on t.oid = ct_as_renter.contract_renter_oid " +
                        "                left join contract ct_as_owner on t.oid = ct_as_owner.contract_renter_oid" +
                        "                where t.oid =:oid " +
                        "group by t.user_name, t.user_rating,  t.user_phone_number, extract(day from now()- t.user_reg_date) " +
                        "order by t.user_name;")
                .setParameter("oid", oid)
                .getSingleResult();
        return CollectionUtils.mapOf(
                "user_name", result[0],
                "user_phone_number", result[1],
                "user_rating", result[2],
                "user_days_reg", result[3],
                "user_send_offer", result[4],
                "user_receive_offer", result[5],
                "user_contracts_as_renter", result[6],
                "user_contracts_as_owner", result[7]
        );
    }

    @Transactional
    public void deleteFile(String oid, String path, String targetManyToManyServiceTable){
        manager.createNativeQuery("call delete_file(cast(:oid as text), cast(:path as text), cast(:target as text))" )
                .setParameter("oid", oid)
                .setParameter("path", path)
                .setParameter("target", targetManyToManyServiceTable)
                .executeUpdate();
    }
}
