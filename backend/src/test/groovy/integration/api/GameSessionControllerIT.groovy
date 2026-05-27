package integration.api

import integration.IntegrationTestSpecification

class GameSessionControllerIT extends IntegrationTestSpecification {

	def 'exposes API home links'() {
		when:
		def response = get('/api')

		then:
		response.statusCode.value() == 200
		response.body._links.self.href ==~ /.*\/api/
		response.body._links.createGameSession.href ==~ /.*\/api\/game-sessions/
		response.body._links.findGameSessionByCode.href.contains('/api/game-sessions/code/')
	}

	def 'creates setup session with adult-confirmed host'() {
		when:
		def response = post('/api/game-sessions', [
				hostNickname : 'Host',
				confirmedAdult: true,
				settings      : [
						startSpiceLevel: 'warmup',
						maxSpiceLevel  : 'courage',
						pace           : 'standard'
				]
		])

		then:
		response.statusCode.value() == 201

		and:
		def session = response.body
		session.code ==~ /AGL-[0-9]{3}/
		session.status == 'setup'
		session._links.self.href ==~ /.*\/api\/game-sessions\/${session.id}/
		session._links.addPlayer.href ==~ /.*\/api\/game-sessions\/${session.id}\/players/
		session._links.start.href ==~ /.*\/api\/game-sessions\/${session.id}\/start/
		session.players[0].nickname == 'Host'
		session.players[0].host == true
		session.players[0].comfortProfile.confirmedAdult == true
		session.settings.minPlayers == 2
		session.settings.maxPlayers == 8
	}

	def 'rejects player without adult confirmation'() {
		when:
		def response = post('/api/game-sessions', [
				hostNickname : 'Host',
				confirmedAdult: false
		])

		then:
		response.statusCode.value() == 409
		response.body.error == 'invalid_game_action'
	}

	def 'starts session and deals four cards to each player'() {
		given:
		def session = createSession('Host')
		addPlayer(session.id, 'Guest')

		when:
		def response = post("/api/game-sessions/${session.id}/start", null)

		then:
		response.statusCode.value() == 200

		and:
		def started = response.body
		started.status == 'in_progress'
		started.currentRound == 1
		started.currentTurnPlayerId == started.players[0].id
		started.players.size() == 2
		started.players.every { it.hand.size() == 4 }
		started.players.every { player ->
			player.hand.every { cardInstance ->
				cardInstance.card.type
				cardInstance.card.spiceLevel
				cardInstance.card.target
				cardInstance.card.boundaries != null
			}
		}
	}

	def 'does not start with fewer than two players'() {
		given:
		def session = createSession('Solo')

		when:
		def response = post("/api/game-sessions/${session.id}/start", null)

		then:
		response.statusCode.value() == 409
		response.body.error == 'invalid_game_action'
	}

	def 'plays and completes current turn card'() {
		given:
		def started = startedTwoPlayerSession()
		def currentPlayer = started.players.find { it.id == started.currentTurnPlayerId }
		def otherPlayer = started.players.find { it.id != currentPlayer.id }
		def cardInstance = playableCard(currentPlayer)

		when:
		def playResponse = post("/api/game-sessions/${started.id}/cards/play",
				playCardBody(currentPlayer, cardInstance, otherPlayer))

		then:
		playResponse.statusCode.value() == 200

		and:
		def afterPlay = playResponse.body
		afterPlay.currentCard.cardInstanceId == cardInstance.instanceId
		afterPlay.currentCard.playerId == currentPlayer.id
		afterPlay.players.find { it.id == currentPlayer.id }.hand.size() == 4

		when:
		def completeResponse = post("/api/game-sessions/${started.id}/current-card/complete", [
				playerId: currentPlayer.id
		])

		then:
		completeResponse.statusCode.value() == 200

		and:
		def afterComplete = completeResponse.body
		afterComplete.currentCard == null
		afterComplete.currentTurnPlayerId == otherPlayer.id
		afterComplete.score[currentPlayer.id] == 1
		afterComplete.atmosphereLevel == 2
	}

	def 'refuses current card without dropping below zero points'() {
		given:
		def started = startedTwoPlayerSession()
		def currentPlayer = started.players.find { it.id == started.currentTurnPlayerId }
		def otherPlayer = started.players.find { it.id != currentPlayer.id }
		def cardInstance = playableCard(currentPlayer)
		assert post("/api/game-sessions/${started.id}/cards/play",
				playCardBody(currentPlayer, cardInstance, otherPlayer)).statusCode.value() == 200

		when:
		def response = post("/api/game-sessions/${started.id}/current-card/refuse", [
				playerId: currentPlayer.id
		])

		then:
		response.statusCode.value() == 200

		and:
		def afterRefuse = response.body
		afterRefuse.currentCard == null
		afterRefuse.currentTurnPlayerId == otherPlayer.id
		afterRefuse.score[currentPlayer.id] == 0
	}

	def 'rolls a six-sided die for an active session'() {
		given:
		def started = startedTwoPlayerSession()

		when:
		def response = post("/api/game-sessions/${started.id}/dice-rolls", [
				playerId: started.currentTurnPlayerId
		])

		then:
		response.statusCode.value() == 200

		and:
		def roll = response.body
		roll.sessionId == started.id
		roll.playerId == started.currentTurnPlayerId
		roll.value >= 1
		roll.value <= 6
	}

	private createSession(String nickname) {
		def response = post('/api/game-sessions', [
				hostNickname : nickname,
				confirmedAdult: true
		])
		assert response.statusCode.value() == 201
		response.body
	}

	private addPlayer(String sessionId, String nickname) {
		def response = post("/api/game-sessions/${sessionId}/players", [
				nickname      : nickname,
				confirmedAdult: true
		])
		assert response.statusCode.value() == 200
		response.body
	}

	private startedTwoPlayerSession() {
		def session = createSession('Host')
		addPlayer(session.id, 'Guest')
		def response = post("/api/game-sessions/${session.id}/start", null)
		assert response.statusCode.value() == 200
		response.body
	}

	private static playableCard(player) {
		player.hand.find { it.card.target != 'chosen_player' } ?: player.hand[0]
	}

	private static playCardBody(currentPlayer, cardInstance, otherPlayer) {
		def body = [
				playerId      : currentPlayer.id,
				cardInstanceId: cardInstance.instanceId
		]
		if (cardInstance.card.target == 'chosen_player') {
			body.targetPlayerId = otherPlayer.id
		}
		body
	}
}
