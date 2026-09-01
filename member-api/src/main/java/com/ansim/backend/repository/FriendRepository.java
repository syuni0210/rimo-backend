package com.ansim.backend.repository;

import com.ansim.backend.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long> {

    List<Friend> findByRequestMemberIdAndStatusCodeAndDeleteYn(
            Long requestMemberId, String statusCode, String deleteYn
    );

    List<Friend> findByReceiveMemberIdAndStatusCodeAndDeleteYn(
            Long receiveMemberId, String statusCode, String deleteYn
    );

    Optional<Friend> findByRequestMemberIdAndReceiveMemberIdAndDeleteYn(
            Long requestMemberId, Long receiveMemberId, String deleteYn
    );

    List<Friend> findByRequestMemberIdOrReceiveMemberIdAndDeleteYn(
            Long requestMemberId, Long receiveMemberId, String deleteYn
    );

    @Query("""
        SELECT COUNT(f)
        FROM Friend f
        WHERE f.deleteYn = 'N'
          AND f.statusCode IN ('F001', 'F002')
          AND (
                (f.requestMemberId = :member1 AND f.receiveMemberId = :member2)
                OR
                (f.requestMemberId = :member2 AND f.receiveMemberId = :member1)
              )
        """)
    long countActiveRelationship(@Param("member1") Long member1, @Param("member2") Long member2);

    @Query("""
        SELECT f
        FROM Friend f
        WHERE f.statusCode = 'F002'
          AND f.deleteYn = 'N'
          AND (f.requestMemberId = :memberId OR f.receiveMemberId = :memberId)
        """)
    List<Friend> findAcceptedFriends(@Param("memberId") Long memberId);

    @Query("""
        SELECT f
        FROM Friend f
        WHERE f.statusCode = 'F002'
          AND f.deleteYn = 'N'
          AND (
                (f.requestMemberId = :member1 AND f.receiveMemberId = :member2)
                OR
                (f.requestMemberId = :member2 AND f.receiveMemberId = :member1)
              )
        """)
    Optional<Friend> findAcceptedRelationship(@Param("member1") Long member1, @Param("member2") Long member2);
}
