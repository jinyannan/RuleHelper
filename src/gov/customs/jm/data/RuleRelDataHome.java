package gov.customs.jm.data;

// Generated May 26, 2015 2:43:33 PM by Hibernate Tools 3.4.0.CR1

import java.util.List;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.SessionFactory;
import static org.hibernate.criterion.Example.create;

/**
 * Home object for domain model class RuleRelData.
 * @see gov.customs.jm.data.RuleRelData
 * @author Hibernate Tools
 */
public class RuleRelDataHome {

	private static final Log log = LogFactory.getLog(RuleRelDataHome.class);

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

	public void persist(RuleRelData transientInstance) {
		log.debug("persisting RuleRelData instance");
		try {
			sessionFactory.getCurrentSession().persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(RuleRelData instance) {
		log.debug("attaching dirty RuleRelData instance");
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(RuleRelData instance) {
		log.debug("attaching clean RuleRelData instance");
		try {
			sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(RuleRelData persistentInstance) {
		log.debug("deleting RuleRelData instance");
		try {
			sessionFactory.getCurrentSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public RuleRelData merge(RuleRelData detachedInstance) {
		log.debug("merging RuleRelData instance");
		try {
			RuleRelData result = (RuleRelData) sessionFactory
					.getCurrentSession().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public RuleRelData findById(gov.customs.jm.data.RuleRelDataId id) {
		log.debug("getting RuleRelData instance with id: " + id);
		try {
			RuleRelData instance = (RuleRelData) sessionFactory
					.getCurrentSession().get("gov.customs.jm.data.RuleRelData",
							id);
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

	public List<RuleRelData> findByExample(RuleRelData instance) {
		log.debug("finding RuleRelData instance by example");
		try {
			List<RuleRelData> results = (List<RuleRelData>) sessionFactory
					.getCurrentSession()
					.createCriteria("gov.customs.jm.data.RuleRelData")
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
