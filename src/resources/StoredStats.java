package resources;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class StoredStats implements Serializable{
	public enum Type{
		MASTERED,
		FAILED,
		FAULTED
	}
	class Stats implements Serializable{
		private static final long serialVersionUID = 1L;
		int mastered;
		int failed;
		int faulted;
		public Stats(){
			mastered=0;failed=0;faulted=0;
		}
		public Stats(Stats stats, Stats stats2){
			this.mastered = stats.mastered + stats2.mastered;
			this.faulted = stats.faulted + stats2.faulted;
			this.failed = stats.failed + stats2.failed;
		}
		public void add(Type t, int i){
			switch(t){
			case MASTERED:
				mastered+=i;
				break;
			case FAILED:
				failed+=i;
				break;
			case FAULTED:
				faulted+=i;
				break;
			}
		}
		public void set(Type t, int i){
			switch(t){
			case MASTERED:
				mastered=i;
				break;
			case FAILED:
				failed=i;
				break;
			case FAULTED:
				faulted=i;
				break;
			}
		}
		public Integer get(Type t){
			switch(t){
			case MASTERED:
				return mastered;
			case FAILED:
				return failed;
			case FAULTED:
				return faulted;
			}
			return null;
		}
		public Integer getTotal(){
			return mastered+failed+faulted;
		}
	}
	private static final long serialVersionUID = 1L;
	private HashMap<String,Stats> stats;
	
	public StoredStats(){
		clearStats();
	}
	public void clearStats(){
		stats = new HashMap<String,Stats>();
	}
	public boolean setStats(Type type, String value, Integer number){
		if(stats.get(value)==null){return false;}
		stats.get(value).set(type, number);
		return true;
	}
	public void addStat(Type type, String value, Integer n){
		if(stats.get(value)==null){
			stats.put(value, new Stats());
		}
		stats.get(value).add(type, n);
	}
	public void addStats(StoredStats other){
		if(this.stats.keySet().equals(other.stats.keySet())){
			for(String key : this.stats.keySet()){
				this.stats.get(key).add(StoredStats.Type.MASTERED, other.getStat(StoredStats.Type.MASTERED,key));
				this.stats.get(key).add(StoredStats.Type.FAULTED, other.getStat(StoredStats.Type.FAULTED,key));
				this.stats.get(key).add(StoredStats.Type.FAILED, other.getStat(StoredStats.Type.FAILED,key));
			}
		}
		
	}
	
	public boolean removeKey(String key){
		return (stats.remove(key)!=null);
	}
	public Collection<String> getKeys(){
		return stats.keySet();
	}
	public Collection<String> getKeys(Type type){
		HashSet<String> set = new HashSet<String>();
		for(String str : stats.keySet()){
			if(stats.get(str).get(type)>0){
				set.add(str);
			}
		}
		return set;
	}
	public Number getTotalStatsOfType(Type type){
		int num = 0;
		for(String str : stats.keySet()){
			num += stats.get(str).get(type);
		}
		return num;
	}
	/**
	 * Get stats for defined type of statistic and key
	 * @param type
	 * @param key
	 * @return
	 */
	public Integer getStat(Type type, String key){
		if(type == Type.MASTERED){return stats.get(key).get(type);}
		else if(type == Type.FAILED){return stats.get(key).get(type);}
		else if(type == Type.FAULTED){return stats.get(key).get(type);}
		else{
			return null;
		}
	}
}
