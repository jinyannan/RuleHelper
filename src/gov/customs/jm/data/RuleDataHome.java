package gov.customs.jm.data;

// Generated Jun 4, 2015 4:42:51 PM by Hibernate Tools 3.4.0.CR1

import java.util.List;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.SessionFactory;
import static org.hibernate.criterion.Example.create;

/**
 * Home object for domain model class RuleData.
 * @see gov.customs.jm.data.RuleData
 * @author Hibernate Tools
 */
public class RuleDataHome {

	private static final Log log = LogFactory.getLog(RuleDataHome.class);

	private final SessionFactory sessionFactory = getSessionFactory();

	protected SessionFactory getSessionFactory() {
		try {
			return (SessionFactory) new InitialContext()
					.lookup("SessionFactory");
		} catch (Exception e) {
			log.error("Could not locate SessionFactory in JNDI", e);
			throw new IllegalStateException(
					"Could not locate SessionFactory in JNDI");
		}
	}

	public void persist(RuleData transientInstance) {
		log.debug("persisting RuleData instance");
		try {
			sessionFactory.getCurrentSession().persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(RuleData instance) {
		log.debug("attaching dirty RuleData instance");
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(RuleData instance) {
		log.debug("attaching clean RuleData instance");
		try {
			sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(RuleData persistentInstance) {
		log.debug("deleting RuleData instance");
		try {
			sessionFactory.getCurrentSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public RuleData merge(RuleData detachedInstance) {
		log.debug("merging RuleData instance");
		try {
			RuleData result = (RuleData) sessionFactory.getCurrentSession()
					.merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public RuleData findById(java.math.BigDecimal id) {
		log.debug("getting RuleData instance with id: " + id);
		try {
			RuleData instance = (RuleData) sessionFactory.getCurrentSession()
					.get("gov.customs.jm.data.RuleData", id);
			if (instance == null) {
				log.debug("get successful, no instance found");
			} else {
				log.debug("get successful, instance found");
			}
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List<RuleData> findByExample(RuleData instance) {
		log.debug("finding RuleData instance by example");
		try {
			List<RuleData> results = (List<RuleData>) sessionFactory
					.getCurrentSession()
					.createCriteria("gov.customs.jm.data.RuleData")
					.add(create(instance)).list();
			log.debug("find by example successful, result size: "
					+ results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}
}
