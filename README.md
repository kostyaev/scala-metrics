# Scala Metrics #

Researching [metrics-scala](https://github.com/erikvanoosten/metrics-scala) which is  the Scala API for [Coda Hale's Metrics](https://github.com/codahale/metrics) library.

	make sbt
	./sbt run

## Requirements ##

[Vagrantfile for Graphite + StatsD](https://github.com/Jimdo/vagrant-statsd-graphite-puppet)

	git clone https://github.com/Jimdo/vagrant-statsd-graphite-puppet.git
	cd vagrant-statsd-graphite-puppet
	vagrant up
	open http://localhost:8080/
