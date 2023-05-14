package wane.study.springsecuritywithbootver3.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author: wan2daaa
 */
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByMemberId(String userId);

}
